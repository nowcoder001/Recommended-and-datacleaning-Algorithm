package DataClean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;

public class GetCleanedContent {
    private Document document;
    private String html = "";

    public GetCleanedContent(String html) {
        this.html = html;
        document = Jsoup.parse(html);
    }


    private String getPs() {

        //获得所有p标签
        Elements links = document.getElementsByTag("p");

//实例化stringbuffer
        StringBuffer buffer = new StringBuffer();
        for (Element link : links) {

//将文本提前出来
            buffer.append(link.text().trim());
        }
        return buffer.toString().trim();

    }
    private String getHs() {


        Elements links = document.getElementsByTag("h1");
        StringBuffer buffer = new StringBuffer();
        for (Element link : links) {

            buffer.append(link.text().trim());
        }
         links = document.getElementsByTag("h2");
         buffer = new StringBuffer();
        for (Element link : links) {


            buffer.append(link.text().trim());
        }

        links = document.getElementsByTag("h3");
        buffer = new StringBuffer();
        for (Element link : links) {


            buffer.append(link.text().trim());
        }
        links = document.getElementsByTag("h4");
        buffer = new StringBuffer();
        for (Element link : links) {


            buffer.append(link.text().trim());
        }
        links = document.getElementsByTag("h5");
        buffer = new StringBuffer();
        for (Element link : links) {


            buffer.append(link.text().trim());
        }
        links = document.getElementsByTag("h6");
        buffer = new StringBuffer();
        for (Element link : links) {


            buffer.append(link.text().trim());
        }
        return buffer.toString().trim();

    }
    private String getTrs() {


        Elements links = document.getElementsByTag("tr");
        StringBuffer buffer = new StringBuffer();
        for (Element link : links) {

            buffer.append(link.text().trim());
        }

        return buffer.toString().trim();

    }
    private String getDivs() {


        Elements links = document.getElementsByTag("div");
        StringBuffer buffer = new StringBuffer();
        for (Element link : links) {

            buffer.append(link.text().trim());
        }

        return buffer.toString().trim();

    }
    private String getLis() {


        Elements links = document.getElementsByTag("li");
        StringBuffer buffer = new StringBuffer();
        for (Element link : links) {

            buffer.append(link.text().trim());
        }

        return buffer.toString().trim();

    }

    public static String htmlRemoveTag(String inputString) {
        if (inputString == null)
            return null;
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;
        try {
            //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
            //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签
            textStr = htmlStr;
            String after = textStr.replaceAll("[a-zA-z0-9<>$&^*%\t\n: /=\"',.{}~()\0_]","");
            textStr = after;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return textStr;// 返回文本字符串
    }
    public String parse() {
        String result = "";
        result += getPs();
//        result +=getDivs();
        result +=getHs();
//        result+=getLis();
//        result +=getTrs();
//        result +=getDivs();
        return htmlRemoveTag(result);
    }
}
