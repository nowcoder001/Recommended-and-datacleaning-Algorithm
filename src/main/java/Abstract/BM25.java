package Abstract;

/**
 *  搜索相关性评分算法
 *  评价搜索词和文档之间相关性的算法
 *  它是一种基于概率检索模型提出的算法
 *  https://www.jianshu.com/p/b4f06594d32f
 *
 *  单词和D之间的相关性
 * 单词和query之间的相关性
 * 每个单词的权重
 * 最后对于每个单词的分数我们做一个求和，就得到了query和文档之间的分数。
 */

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BM25 {
    /**
     * 文档句子的个数
     */
    int D;

    /**
     * 文档句子的平均长度
     */
    double avgdl;

    /**
     * 拆分为[句子[单词]]形式的文档
     */
    List<List<String>> docs;

    /**
     * 文档中每个句子中的每个词与词频
     */
    Map<String, Integer>[] f;

    /**
     * 文档中全部词语与出现在几个句子中
     */
    Map<String, Integer> df;

    /**
     * IDF
     */
    Map<String, Double> idf;

    /**
     * 调节因子
     */
    final static float k1 = 1.5f;

    /**
     * 调节因子
     */
    final static float b = 0.75f;
    public BM25(List<List<String>> docs)
    {
        this.docs = docs;
        D = docs.size();
        //计算文档中句子的平均长度  总词数/句子总数
        for (List<String> sentence : docs)
        {
            avgdl += sentence.size();
        }
        avgdl /= D;
        f = new Map[D];
        df = new TreeMap<String, Integer>();
        idf = new TreeMap<String, Double>();
        init();
    }

    /**
     * 在构造时初始化自己的所有参数
     *
     */
    private void init()
    {
        int index = 0;//index表示现在是文档中的第几句话

        for (List<String> sentence : docs)
        {
            //对于每个句子的分词结果  计算 分词在这个句子中出现的频率 为tf   存成String int的映射形式
            Map<String, Integer> tf = new TreeMap<String, Integer>();
            for (String word : sentence)

            {
                //计算每个值出现的频数  作为tf
                Integer freq = tf.get(word);
                freq = (freq == null ? 0 : freq) + 1;
                tf.put(word, freq);
            }
            f[index] = tf;//存储每句话对应的tf值Map

            //根据tf值算df值  计算每个词出现在几个句子中
            for (Map.Entry<String, Integer> entry : tf.entrySet())
            {

                String word = entry.getKey();
                Integer freq = df.get(word);
                freq = (freq == null ? 0 : freq) + 1;
                df.put(word, freq);
            }
            ++index;
        }
        //根据df计算idf   公司为log(D - freq + 0.5) - Math.log(freq + 0.5)  D为文档中句子个数  0.5为平滑项
        for (Map.Entry<String, Integer> entry : df.entrySet())
        {
            //计算逆文档频率 idf
            String word = entry.getKey();
            Integer freq = entry.getValue();
            idf.put(word, Math.log(D - freq + 0.5) - Math.log(freq + 0.5));
        }
    }

    /**
     * 计算相似度 最终得到一个句子 与对应index句子的相关性得分
     * @param sentence
     * @param index
     * @return
     */
    public double sim(List<String> sentence, int index)
    {
        double score = 0;
        //对于一句话中的每一个单词  计算这个单词  与其他句子的相关性得分 这个得分用BM25计算出
        for (String word : sentence)
        {
            if (!f[index].containsKey(word)) continue;
            int d = docs.get(index).size();//index对应句子的词的个数
            Integer wf = f[index].get(word);//在index对应句子中 词word出现的次数
            //，参数b的作用是调整文档长度对相关性影响的大小。b越大，文档长度的对相关性得分的影响越大，反之越小。而文档的相对长度越长，K值将越大，则相关性得
            //分会越小。这可以理解为，当文档较长时，包含qi的机会越大，因此，同等fi的情况下，长文档与qi的相关性应该比短文档与qi的相关性弱。
            score += (idf.get(word) * wf * (k1 + 1)
                    / (wf + k1 * (1 - b + b * d
                    / avgdl)));
        }
        //最终得到一个句子  与对应index句子的相关性得分
        return score;
    }

    /**
     * 计算整体的相似度  计算每一个句子与其他所有句子的相似度
     * @param sentence
     * @return
     */
    public double[] simAll(List<String> sentence)
    {
        double[] scores = new double[D];
        for (int i = 0; i < D; ++i)
        {
            scores[i] = sim(sentence, i);
        }
        return scores;
    }
}
