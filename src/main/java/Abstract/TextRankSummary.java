package Abstract;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;

import java.util.*;

/**
 * TextRank 自动摘要
 */
public class TextRankSummary {
    /**
     * 阻尼系数，一般取值为0.85
     */
    final double d = 0.85f;
    /**
     * 最大迭代次数
     */
    final int max_iter = 200;
    final double min_diff = 0.001f;
    /**
     * 文档句子的个数
     */
    int D;
    /**
     * 拆分为[句子[单词]]形式的文档
     */
    List<List<String>> docs;
    /**
     * 排序后的最终结果 score <-> index
     * 使用treemap 因为treemap自动按key排序
     */
    TreeMap<Double, Integer> top;

    /**
     * 句子和其他句子的相关程度
     */
    double[][] weight;
    /**
     * 该句子和其他句子相关程度之和
     */
    double[] weight_sum;
    /**
     * 迭代之后收敛的权重
     */
    double[] vertex;

    /**
     * BM25相似度
     */
    BM25 bm25;
    public TextRankSummary(List<List<String>> docs)
    {
        this.docs = docs;
        bm25 = new BM25(docs);
        D = docs.size();
        weight = new double[D][D];// 句子和其他句子的相关程度
        weight_sum = new double[D];
        vertex = new double[D];//该句子和其他句子相关程度之和
        top = new TreeMap<Double, Integer>(Collections.reverseOrder());//选出排名靠前的句子
        solve();
    }

    /**
     * 构造矩阵计算最相似的内容
     */
    private void solve()
    {
        int cnt = 0;
        //对于文档中的每一句话 都要计算他与其他每一句话的相似程度  以及他与其他所有句子的总相似程度
        for (List<String> sentence : docs)
        {
            //对于文档中的每一个句子 计算它与文档中其他所有句子的相似程度
            double[] scores = bm25.simAll(sentence);


            weight[cnt] = scores; //将计算结果存储在矩阵中
            weight_sum[cnt] = sum(scores) - scores[cnt]; // 减掉自己，自己跟自己肯定最相似
            vertex[cnt] = 1.0;//所有句子权重初始化都为1
            ++cnt;
        }
        //开始使用textrank算法进行迭代 200轮
        for (int iter = 0; iter < max_iter; ++iter)
        {
            double[] m = new double[D];
            double max_diff = 0;
            for (int i = 0; i < D; ++i)
            {
                m[i] = 1 - d;
                for (int j = 0; j < D; ++j)
                {
                    if (j == i || weight_sum[j] == 0) continue;
                    m[i] += (d * weight[j][i] / weight_sum[j] * vertex[j]);
                }
                double diff = Math.abs(m[i] - vertex[i]);
                if (diff > max_diff)
                {
                    max_diff = diff;
                }
            }
            vertex = m;
            if (max_diff <= min_diff) break;
        }
        // 然后进行排序 输出权重最大的那个
        for (int i = 0; i < D; ++i)
        {
            top.put(vertex[i], i);
        }
    }

    /**
     * 获取前几个关键句子
     * @param size 要几个
     * @return 关键句子的下标
     */
    public int[] getTopSentence(int size)
    {
        Collection<Integer> values = top.values();
        size = Math.min(size, values.size());
        int[] indexArray = new int[size];
        Iterator<Integer> it = values.iterator();
        for (int i = 0; i < size; ++i)
        {
            indexArray[i] = it.next();
        }
        return indexArray;
    }

    /**
     * 简单的求和
     * @param array
     * @return
     */
    private static double sum(double[] array)
    {
        double total = 0;
        for (double v : array)
        {
            total += v;
        }
        return total;
    }

    /**
     * 将文章分割为句子  分割依据为标点符号
     * @param document
     * @return
     */
    static List<String> spiltSentence(String document)
    {
        List<String> sentences = new ArrayList<String>();
        if (document == null) return sentences;
        for (String line : document.split("[\r\n]"))
        {
            line = line.trim();
            if (line.length() == 0) continue;
            for (String sent : line.split("[，,。:：“”？?！!；;]"))
            {
                sent = sent.trim();
                if (sent.length() == 0) continue;
                sentences.add(sent);
            }
        }

        return sentences;
    }

    /**
     * 是否应当将这个term纳入计算，词性属于名词、动词、副词、形容词
     * @param term
     * @return 是否应当
     */
    public static boolean shouldInclude(Term term)
    {
        return CoreStopWordDictionary.shouldInclude(term);
    }

    /**
     * 一句话调用接口
     * @param document 目标文档
     * @param size 需要的关键句的个数
     * @return 关键句列表
     */
    public static String getTopSentenceList(String document, int size)
    {
        List<String> sentenceList = spiltSentence(document);
        List<List<String>> docs = new ArrayList<List<String>>(); //存储句子分词后的结果
        for (String sentence : sentenceList)
        {
            //利用HanLP的接口将句子进行分词
            List<Term> termList = HanLP.segment(sentence);
            List<String> wordList = new LinkedList<String>();
            for (Term term : termList)
            {   //是否应当将这个term纳入计算，词性属于名词、动词、副词、形容词
                if (shouldInclude(term))
                {
                    wordList.add(term.word);
                }
            }
            docs.add(wordList);
        }
        TextRankSummary textRankSummary = new TextRankSummary(docs);
        //然后获取排名最高的几个句子
        int[] topSentence = textRankSummary.getTopSentence(size);
        List<String> resultList = new LinkedList<String>();
        String result = "";
        for (int i : topSentence)
        {
//            resultList.add(sentenceList.get(i));
            result += sentenceList.get(i) +",";
        }
        return result;
    }


    public static void main(String[] args)
    {
        String document = "四海网讯网讯，近日，有媒体报道称：章子怡真怀孕了!报道还援引知情人士消息称，" +
                "“章子怡怀孕大概四五个月，预产期是年底前后，现在已经不接工作了。”这到底是怎么回事?消息是真是假?针对此消息，" +
                "23日晚8时30分，华西都市报记者迅速联系上了与章子怡家里关系极好的知情人士，这位人士向华西都市报记者证实说：“子怡这次确实怀孕了。" +
                "她已经36岁了，也该怀孕了。章子怡怀上汪峰的孩子后，子怡的父母亲十分高兴。子怡的母亲，已开始悉心照料女儿了。子怡的预产期大概是今年12月底。" +
                "”当晚9时，华西都市报记者为了求证章子怡怀孕消息，又电话联系章子怡的亲哥哥章子男，但电话通了，一直没有人接听。有关章子怡怀孕的新闻自从2013年9月份章子怡和汪峰恋情以来，" +
                "就被传N遍了!不过，时间跨入2015年，事情却发生着微妙的变化。2015年3月21日，章子怡担任制片人的电影《从天儿降》开机，在开机发布会上几张合影，让网友又燃起了好奇心：" +
                "“章子怡真的怀孕了吗?”但后据证实，章子怡的“大肚照”只是影片宣传的噱头。过了四个月的7月22日，《太平轮》新一轮宣传，章子怡又被发现状态不佳，不时深呼吸，不自觉想捂住肚子" +
                "，又觉得不妥。然后在8月的一天，章子怡和朋友吃饭，在酒店门口被风行工作室拍到了，疑似有孕在身!今年7月11日，汪峰本来在上海要举行演唱会，后来因为台风“灿鸿”取消了。" +
                "而消息人士称，汪峰原来打算在演唱会上当着章子怡的面宣布重大消息，" +
                "而且章子怡已经赴上海准备参加演唱会了，怎知遇到台风，只好延期，相信9月26日的演唱会应该还会有惊喜大白天下吧。";
        System.out.println(TextRankSummary.getTopSentenceList(document, 3));
    }

}
