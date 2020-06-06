package similarity;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static similarity.Tokenizer.segment;


/**
 * 余弦相似度计算
 * 判定方式：余弦相似度，通过计算两个向量的夹角余弦值来评估他们的相似度
 * 余弦夹角原理：
 * 向量a=(x1,y1),向量b=(x2,y2)
 * similarity=a.b/|a|*|b|
 * a.b=x1x2+y1y2
 * |a|=根号[(x1)^2+(y1)^2],|b|=根号[(x2)^2+(y2)^2]
 *
 */
public class CosineSimilarity {

    protected static final Logger LOGGER = LoggerFactory.getLogger(CosineSimilarity.class);
    public static double getCosineSimilarity(List<Word> words1, List<Word> words2) {
        // 词频标注词的权重
        taggingWeightByFrequency(words1, words2);
        // 权重容器   可以根据词语查询权重
        Map<String, Float> weightMap1 = getFastSearchMap(words1);
        Map<String, Float> weightMap2 = getFastSearchMap(words2);
        Set<Word> words = new HashSet<>();
        words.addAll(words1);
        words.addAll(words2);

        //保证计数原子性的容器
        AtomicFloat ab = new AtomicFloat();// a.b
        AtomicFloat aa = new AtomicFloat();// |a|的平方
        AtomicFloat bb = new AtomicFloat();// |b|的平方
        // 计算
        words.parallelStream().forEach(word -> {
                    Float x1 = weightMap1.get(word.getName());
                    Float x2 = weightMap2.get(word.getName());
                    if (x1 != null && x2 != null) {
                        //x1x2
                        float oneOfTheDimension = x1 * x2;
                        //+
                        ab.addAndGet(oneOfTheDimension);
                    }
                    if (x1 != null) {
                        //(x1)^2
                        float oneOfTheDimension = x1 * x1;
                        //+
                        aa.addAndGet(oneOfTheDimension);
                    }
                    if (x2 != null) {
                        //(x2)^2
                        float oneOfTheDimension = x2 * x2;
                        //+
                        bb.addAndGet(oneOfTheDimension);
                    }
                });
        //|a|
        double aaa = Math.sqrt(aa.doubleValue());
        //|b|
        double bbb = Math.sqrt(bb.doubleValue());
        //使用BigDecimal保证精确计算浮点数
        //|a|*|b|
        //double aabb = aaa * bbb;
        BigDecimal aabb = BigDecimal.valueOf(aaa).multiply(BigDecimal.valueOf(bbb));
        //similarity=a.b/|a|*|b|
        //double cos = ab.get() / aabb.doubleValue();
        double cos = BigDecimal.valueOf(ab.get()).divide(aabb, 9, BigDecimal.ROUND_HALF_UP).doubleValue();
        return cos;
    }

    /**
     *
     * @param words1
     * @param words2
     */
    protected static void taggingWeightByFrequency(List<Word> words1, List<Word> words2) {
        if (words1.get(0).getWeight() != null || words2.get(0).getWeight() != null) {
            return;
        }//AtomicInteger java的并发原子类
        Map<String, AtomicInteger> frequency1 = getFrequency(words1);
        Map<String, AtomicInteger> frequency2 = getFrequency(words2);
//        输出词频统计信息
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("词频统计1：\n{}", getWordsFrequencyString(frequency1));
            LOGGER.debug("词频统计2：\n{}", getWordsFrequencyString(frequency2));
        }
        // 标注权重
        words1.parallelStream().forEach(word -> word.setWeight(frequency1.get(word.getName()).floatValue()));
        words2.parallelStream().forEach(word -> word.setWeight(frequency2.get(word.getName()).floatValue()));
    }

    /**
     * 统计词频
     *
     * @param words 词列表
     * @return 词频统计图
     */
    private static Map<String, AtomicInteger> getFrequency(List<Word> words) {
        Map<String, AtomicInteger> freq = new HashMap<>();
       for(Word word : words)
       {
           // key存在，则不操作，key不存在，则赋值一对新的（key，value）
            freq.computeIfAbsent(word.getName(),k -> new AtomicInteger()).incrementAndGet();
       }
        return freq;
    }

    /**
     * 词频统计信息
     *
     *
     * @param frequency 词频
     * @return
     */
    private static String getWordsFrequencyString(Map<String, AtomicInteger> frequency) {
        StringBuilder str = new StringBuilder();
        if (frequency != null && !frequency.isEmpty()) {
            AtomicInteger integer = new AtomicInteger();
            //将frequency 经过排序后 输出结果
            frequency.entrySet()
                    .stream()
                    .sorted((a, b) -> b.getValue().get() - a.getValue().get())
                    .forEach(i -> str.append("\t")
                            .append(integer.incrementAndGet())
                            .append("、")
                            .append(i.getKey())
                            .append("=")
                            .append(i.getValue())
                            .append("\n")
                    );
        }
        str.setLength(str.length() - 1);
        return str.toString();
    }
    /**
     * 构造权重快速搜索容器
     *
     * @param words
     * @return
     */
    protected static Map<String, Float> getFastSearchMap(List<Word> words) {
        //线程安全的ConcurrentHashMap
        Map<String, Float> weightMap = new ConcurrentHashMap<>();
        if (words == null) return weightMap;
        words.parallelStream().forEach(i -> {
            if (i.getWeight() != null) {
                weightMap.put(i.getName(), i.getWeight());
            } else {
                LOGGER.error("no word weight info:" + i.getName());
            }
        });
        return weightMap;
    }

    public static void main(String[] args)
    {
        List<Word>  words= segment("CSS");
        List<Word>  words2= segment("CSS");
       System.out.println( getCosineSimilarity(words,words2));
    }

}
