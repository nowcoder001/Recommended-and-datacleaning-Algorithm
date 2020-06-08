package DataClean;

import Abstract.TextRankKeyword;
import Abstract.TextRankSummary;
import com.google.common.base.Preconditions;

import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Integer.min;

public class ArticleClean {
    /**目前去除<a></a>,<img>,<p></p>,<br>,<div></div>,<iframe></iframe>,<span></span>,<h></h>,<font></font>标签,将其替换为空*/
    public static String getHtmlSplit(String html){
        html=html.replaceAll("<a href[^>]*>", "");
        html=html.replaceAll("</a>", "");
        html=html.replaceAll("<img[^>]*>", "");
        html=html.replaceAll("<p[^>]*>", "");
        html=html.replaceAll("</p>", "");
        html=html.replaceAll("<br[^>]*>", "");
        html=html.replaceAll("<div[^>]*>", "");
        html=html.replaceAll("</div>", "");
        html=html.replaceAll("<iframe src[^>]*>", "");
        html=html.replaceAll("</iframe>", "");
        html=html.replaceAll("<span style[^>]*>", "");
        html=html.replaceAll("</span>", "");
        html=html.replaceAll("<h1[^>]*>", "");
        html=html.replaceAll("</h1>", "");
        html=html.replaceAll("<h2[^>]*>", "");
        html=html.replaceAll("</h2>", "");
        html=html.replaceAll("<h3[^>]*>", "");
        html=html.replaceAll("</h3>", "");
        html=html.replaceAll("<h4[^>]*>", "");
        html=html.replaceAll("</h4>", "");
        html=html.replaceAll("<h5[^>]*>", "");
        html=html.replaceAll("</h5>", "");
        html=html.replaceAll("<font style[^>]*>", "");
        html=html.replaceAll("</font>", "");
        html=html.replaceAll("<ul  style[^>]*>", "");
        html=html.replaceAll("</ul>", "");
        html=html.replaceAll("<li  style[^>]*>", "");
        html=html.replaceAll("</li>", "");
        html=html.replaceAll("<table  style[^>]*>", "");
        html=html.replaceAll("</table>", "");

        html=html.replaceAll("<thead  style[^>]*>", "");
        html=html.replaceAll("</thead>", "");

        html=html.replaceAll("<tr style[^>]*>", "");
        html=html.replaceAll("</tr>", "");

        html=html.replaceAll("<th  style[^>]*>", "");
        html=html.replaceAll("</th>", "");

        html=html.replaceAll("<td  style[^>]*>", "");
        html=html.replaceAll("</td>", "");


        html=html.replaceAll("<ul  [^>]*>", "");
        html=html.replaceAll("</ul>", "");
        html=html.replaceAll("<li  [^>]*>", "");
        html=html.replaceAll("</li>", "");
        html=html.replaceAll("<table  [^>]*>", "");
        html=html.replaceAll("</table>", "");

        html=html.replaceAll("<thead  [^>]*>", "");
        html=html.replaceAll("</thead>", "");

        html=html.replaceAll("<tr [^>]*>", "");
        html=html.replaceAll("</tr>", "");

        html=html.replaceAll("<th  [^>]*>", "");
        html=html.replaceAll("</th>", "");

        html=html.replaceAll("<td  [^>]*>", "");
        html=html.replaceAll("</td>", "");

        String regEx = "(<span\\s*[^>]*>(.*?)<\\/span>)|(<sapn\\s*[^>]*>([\\s\\S]*?)<\\/sapn>)";
        html=html.replaceAll(regEx, "");
        String regEx2 = "(<code\\s*[^>]*>(.*?)<\\/span>)|(<sapn\\s*[^>]*>([\\s\\S]*?)<\\/code>)";
        html=html.replaceAll(regEx2, "");
//        html=html.replaceAll("<th [^>]*>", "");
//        html=html.replaceAll("</th>", "");
//        html=html.replaceAll("<th [^>]*>", "");
//        html=html.replaceAll("</th>", "");
        html=html.replaceAll("&nbsp", "");
        return html;
    }


    /**
     * 获取博客的标签 ——有的博客本身有标签 但是需要统一成系统有的标签   有的博客没有标签，就需要构造标签
     * @param tagList   系统定义好的标签
     * @param articleBean  文章的信息
     * @return
     */
    public static List<String> getTag(List<Tag> tagList,ArticleBean articleBean)
{
    String tags = articleBean.getTags();//如果文章本身有标签 存在这里
    String clean_content = articleBean.getClean_content();//清洗后的文章内容
    String title = articleBean.getTitle();//文章的标题
    Map<String,Integer> map = new HashMap<>();
    List<String> resultTag = new ArrayList<>();//存储提取标签的结果

    if(!clean_content.equals("")) //当内容不为空时才能提取tag

    {
        //当这篇博客本身就有tag时
            if(!tags.equals(""))
            {
                String temp_tag [] = tags.split(",");
                //对比已有标签和系统定义标签的相似度标签的相似度
                /**
                 * 对比编辑距离  对比前需要将汉字全部转化为拼音 方便比对编辑距离
                 *
                 */
                for(int i = 0 ; i < temp_tag.length ; i++)
                {
                        for(Tag tag :tagList)
                        {

                                String default_tag = Word2PinYin(tag.getName());
                                String desc [] = tag.getDescription().split("#");
                                String now_tag = Word2PinYin(temp_tag[i]);

                                if(StringHasChinese(tag.getName()) || StringHasChinese(temp_tag[i]))
                                {
                                    if(editDistance(default_tag,now_tag) <3)
                                    {
                                        if(!map.containsKey(tag.getName())) map.put(tag.getName(),0);
                                        map.put(tag.getName(),map.get(tag.getName()) +1);
                                        resultTag.add(tag.getName());
                                        continue;
                                    }
                                }
                                else
                                {
                                    if(editDistance(default_tag,now_tag) ==0)
                                    {

                                        if(!map.containsKey(tag.getName())) map.put(tag.getName(),0);
                                        map.put(tag.getName(),map.get(tag.getName()) +1);
                                        resultTag.add(tag.getName());
                                        continue;
                                    }
                                }


                                for(int k = 0 ; k<desc.length ; k++)
                                {
                                    if((StringHasChinese(now_tag) || StringHasChinese(desc[k]) )&& !desc[k].equals(""))
                                    {
                                        if(editDistance(Word2PinYin(desc[k]),now_tag) <3)
                                        {
                                            if(!map.containsKey(tag.getName())) map.put(tag.getName(),0);
                                            map.put(tag.getName(),map.get(tag.getName()) +1);
                                            resultTag.add(tag.getName());
                                            continue;
                                        }
                                    }
                                    else {

                                        if(editDistance(Word2PinYin(desc[k]),now_tag) ==0)
                                        {
                                            if(!map.containsKey(tag.getName())) map.put(tag.getName(),0);
                                            map.put(tag.getName(),map.get(tag.getName()) +1);
                                            resultTag.add(tag.getName());
                                            continue;
                                        }
                                    }

                                }
               }}
            }
            /**
                 * 如果没有自带标签 或者自带标签与系统中的标签无法匹配上时，需要我们根据博客的标题和文字内容进行 字串对比 看能不能匹配到对应的标签
                 */

                for(Tag tag :tagList)
                {

                    String default_tag = Word2PinYin(tag.getName());
                    String desc [] = tag.getDescription().split("#");
                    if(clean_content.toLowerCase().contains(default_tag.toLowerCase()))
                    {
                        if(!map.containsKey(tag.getName())) map.put(tag.getName(),0);
                        map.put(tag.getName(),map.get(tag.getName()) +1);
                        resultTag.add(tag.getName());
                        continue;
                    }
                    else if(title.toLowerCase().contains(default_tag.toLowerCase()))
                    {
                        if(!map.containsKey(tag.getName())) map.put(tag.getName(),0);
                        map.put(tag.getName(),map.get(tag.getName()) +1);
                        resultTag.add(tag.getName());
                        continue;
                    }else
                    {
                        for(int k = 0 ; k<desc.length ; k++)
                        {
                            if(clean_content.toLowerCase().contains(desc[k].toLowerCase()) && !desc[k].equals(""))
                            {
                                if(!map.containsKey(tag.getName())) map.put(tag.getName(),0);
                                map.put(tag.getName(),map.get(tag.getName()) +1);
                                resultTag.add(tag.getName());
                                continue;
                            }
                        }
                    }



                }
    }
    List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
    Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            if(o2.getValue() > o1.getValue())
                return 1;
            else  if(o2.getValue() < o1.getValue())
                return -1;
            else return 0;
//
        }
    });
    int index = 0;
    List<String> result = new ArrayList<>();
    for(Map.Entry<String, Integer> t:list){
        result.add(t.getKey());
        if(++index >2) break;
    }
    return result;
//    return  new ArrayList<String>(new HashSet<String>(resultTag));

}

    /**
     * 有了标签之后 根据博客的标签 匹配对应的博客分类
     * @param classify_list
     * @param tag_list
     * @return
     */
    public static List<String> getClassify(List<Classify> classify_list ,List<String> tag_list)
{
    List<String> classify_result = new ArrayList<>();
    for(String tag :tag_list)
    {
        String deal_tag = Word2PinYin(filtration(tag));
        for(Classify classify :classify_list)
        {
            String classify_desc [] = classify.getDescription().split("#");
            for(int i = 0 ; i< classify_desc.length ; i++)
            {
                    String deal_decs = Word2PinYin(filtration(classify_desc[i]));
                    if(editDistance(deal_decs,deal_tag) == 0)
                    {
                        classify_result.add(classify.getName());
                    }
            }
        }
    }
   return new ArrayList<String>(new HashSet<String>(classify_result));
}

    /**
     * 正则表达式 去除特殊字符
     * @param str
     * @return
     */
    public static String filtration(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}:;\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？']";
        str = Pattern.compile(regEx).matcher(str).replaceAll("").trim();
        return str;
    }

    /**
     * 利用动态规划求编辑距离
     * @param str1
     * @param str2
     * @return
     */
    public static int editDistance(String str1, String str2) {
        Preconditions.checkNotNull(str1);
        Preconditions.checkNotNull(str2);

        int len1 = str1.length();
        int len2 = str2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 0; i < len1; i++) {
            char c1 = str1.charAt(i);
            for (int j = 0; j < len2; j++) {
                char c2 = str2.charAt(j);
                //if last two chars equal
                if (c1 == c2) {
                    //update dp value for +1 length
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    dp[i + 1][j + 1] = 1 + min(dp[i+1][j], min(dp[i][j+1], dp[i][j]));
                }
            }
        }

        return  dp[len1][len2];
    }

    /**
     * 将有中文的字符串转成拼音
     * @param str
     * @return
     */
    public static String Word2PinYin(String str)
    {
        StringBuffer sb = new StringBuffer();
        for(int i = 0 ; i<str.length() ; i++)
        {
            char temp = str.charAt(i);
            //如果是小写字母
            if(temp >=97 && temp <= 122)
            {
                sb.append(temp);
            }
            else if(temp >=65 && temp<=90)  //如果是大写字母  转化成小写字母
            {
                sb.append(Character.toLowerCase(temp));
            }
            else if(isChinese(temp))  //如果是中文
            {
                sb.append(PinYinUtil.getStringPinYin(String.valueOf(temp)));
            }
            else //
            {
                sb.append(temp);
            }

        }
        return sb.toString();
    }

    /**
     * 判断一个字符串中是否含有中文
     * @param str
     * @return
     */
    public static boolean StringHasChinese(String str)
    {
        for(int i = 0 ; i< str.length() ; i++)
        {
            if(isChinese(str.charAt(i)))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断一个字符是否是中文
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeScript sc = Character.UnicodeScript.of(c);
        if (sc == Character.UnicodeScript.HAN) {
            return true;
        }

        return false;
    }

    public static void main(String [] args)
    {
        GetMysqlData getMysqlData = new GetMysqlData();
        List<ArticleBean>  articleBeans  =getMysqlData.getArticle();//从数据库中取出所有文章
        List<Tag> tagList = getMysqlData.getTag();//从数据库取出所有tag信息
        List<Classify> classifyList = getMysqlData.getClassify();//从数据库中取出所有分类信息
        for(ArticleBean articleBean:articleBeans) {
            int id = articleBean.getId();
            String title = articleBean.getTitle();
            String content = articleBean.getContent();
            String tags = articleBean.getTags();
            GetCleanedContent GetCleanedContent = new GetCleanedContent(content); //将博客内容 带html标签的内容  使用jsoup提取对应标签中的内容
            String clean_content = GetCleanedContent.parse();
            String keyword = new TextRankKeyword().getKeyword(title, clean_content);//使用textrank算法提取关键词
            String summary = TextRankSummary.getTopSentenceList(clean_content, 3);//使用textrank算法提取摘要
            articleBean.setKeyword(keyword);
            articleBean.setClean_content(clean_content);
            articleBean.setSummary(summary);
            List<String > result_tag = getTag(tagList,articleBean);//提取标签

            List<String > result_classify =getClassify(classifyList,result_tag);//提取分类
            String str_tag = "";
            for(String s :result_tag)
            {
                str_tag += s+",";
            }
            String str_classify  = "";
            for(String s :result_classify)
            {
                str_classify += s+",";
            }


            if (str_tag.length() != 0)
            {
                System.out.println(id+"   " +tags);
                System.out.println(str_tag.substring(0,str_tag.length()-1));
                System.out.println(str_classify);
                getMysqlData.UpdateArticle(id,str_tag.substring(0,str_tag.length()-1),str_classify,clean_content,keyword,summary);
            }else
            {
                System.out.println(id+"   " +tags);
                System.out.println(str_tag);
                System.out.println(str_classify);
                getMysqlData.UpdateArticle(id,str_tag,str_classify,clean_content,keyword,summary);
            }


        }
    }
}
