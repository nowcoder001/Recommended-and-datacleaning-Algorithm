package DataClean;

import Abstract.TextRankKeyword;
import Abstract.TextRankSummary;
import com.google.common.base.Preconditions;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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


public static List<String> getTag(List<Tag> tagList,ArticleBean articleBean)
{
    String tags = articleBean.getTags();
    String clean_content = articleBean.getClean_content();
    String title = articleBean.getTitle();
    String keywords[] = articleBean.getKeyword().split(",");
    //当内容不为空时才能提取tag
    List<String> resultTag = new ArrayList<>();
    if(!clean_content.equals(""))
    {
        //当这篇博客本身就有tag时
            if(!tags.equals(""))
            {
                String temp_tag [] = tags.split(",");
                //对比已有标签和 此标签的相似度
                for(int i = 0 ; i < temp_tag.length ; i++)
                {
                        for(Tag tag :tagList)
                        {
                            String default_tag = Word2PinYin(tag.getName());
                            String desc [] = tag.getDescription().split(" ");
                            String now_tag = Word2PinYin(temp_tag[i]);
                            if(StringHasChinese(tag.getName()) || StringHasChinese(temp_tag[i]))
                            {
                                if(editDistance(default_tag,now_tag) <3)
                                {

                                    resultTag.add(tag.getName());
                                    continue;
                                }
                            }
                            else
                            {
                                if(editDistance(default_tag,now_tag) ==0)
                                {

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
                                            resultTag.add(tag.getName());
                                            continue;
                                        }
                                    }
                                    else {

                                        if(editDistance(Word2PinYin(desc[k]),now_tag) ==0)
                                        {
                                            resultTag.add(tag.getName());
                                            continue;
                                        }
                                    }

                                }


                        }
                }
            }

                for(Tag tag :tagList)
                {
                    String default_tag = Word2PinYin(tag.getName());
                    String desc [] = tag.getDescription().split(" ");
                    if(clean_content.toLowerCase().contains(default_tag.toLowerCase()))
                    {
                        resultTag.add(tag.getName());
                        continue;
                    }
                    else if(title.toLowerCase().contains(default_tag.toLowerCase()))
                    {
                        resultTag.add(default_tag);
                        continue;
                    }else
                    {
                        for(int k = 0 ; k<desc.length ; k++)
                        {
                           if(clean_content.toLowerCase().contains(desc[k].toLowerCase()) && !desc[k].equals(""))
                           {
                               System.out.println(clean_content.toLowerCase());
                               System.out.println(desc[k].toLowerCase());
                               resultTag.add(tag.getName());
                               continue;
                           }
                        }
                    }

                }

    }
    return resultTag;

}
//求编辑距离
public static int editDistance(String str1, String str2) {
        Preconditions.checkNotNull(str1);
        Preconditions.checkNotNull(str2);

        int len1 = str1.length();
        int len2 = str2.length();

        // len1+1, len2+1, because finally return dp[len1][len2]
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
                    dp[i + 1][j + 1] = 1 + min(dp[i+1][j], min(dp[i][j+1], dp[i][j])); // 这里不需要递归实现了
                }
            }
        }

        return  dp[len1][len2];
    }
    //将有中文的字符串转成拼音
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
    //判断一个字符是否是中文
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
        List<ArticleBean>  articleBeans  =getMysqlData.getArticle();
        List<Tag> tagList = getMysqlData.getTag();
        for(ArticleBean articleBean:articleBeans) {
            int id = articleBean.getId();
            String title = articleBean.getTitle();
            String content = articleBean.getContent();
            String tags = articleBean.getTags();
            GetCleanedContent GetCleanedContent = new GetCleanedContent(content);
            String clean_content = GetCleanedContent.parse();
            String keyword = new TextRankKeyword().getKeyword(title, clean_content);
            String summary = TextRankSummary.getTopSentenceList(clean_content, 3);
            articleBean.setKeyword(keyword);
            articleBean.setClean_content(clean_content);
            articleBean.setSummary(summary);
            List<String > result_tag = getTag(tagList,articleBean);
            String str = "";
            for(String s :result_tag)
            {
                str += s+",";
            }
            System.out.println(id+"   " +tags);
            getMysqlData.UpdateArticle(id,str,clean_content,keyword,summary);

        }
    }
}
