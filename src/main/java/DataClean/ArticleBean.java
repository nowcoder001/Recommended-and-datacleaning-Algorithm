package DataClean;



import java.util.Date;


public class ArticleBean {
    private int id;
    private String title;
    private String url;
    private String content;
    private String tags;
    private Date updateTime;
    private String clean_content;
    private String keyword;
    private String summary;


    public ArticleBean(int id, String title, String content, String tags) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.tags = tags;

    }

    public void setClean_content(String clean_content) {
        this.clean_content = clean_content;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getClean_content() {
        return clean_content;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getSummary() {
        return summary;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public String getTags() {
        return tags;
    }

    public Date getUpdateTime() {
        return updateTime;
    }
}
