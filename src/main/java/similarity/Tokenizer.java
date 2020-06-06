package similarity;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * 对中文分词的封装，（xuming对HanLP改进版）的调用
 * 对分词器的调用采用了单例模式，实现需要时的延迟加载。
 */
public class Tokenizer {
    private static final Logger logger = LoggerFactory.getLogger(Tokenizer.class);

    /**
     *
     * 输入为句子  输出的结果为 分词以及词性  封装在Word类中
     * @param sentence
     * @return
     */
    public  static List<Word> segment(String sentence) {
        List<Word> results = new ArrayList<Word>();

        //调用  HanLP的segment方法进行分词
        List<Term> termList = HanLP.segment(sentence);
       for(Term i :termList)
       {
           Word temp  = new Word(i.word,i.nature.name());
           results.add(temp);
       }
        return results;
    }



}
