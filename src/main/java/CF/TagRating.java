package CF;

/**
 * 每个标签的点击率
 */
public class TagRating {

    String tag_id;//标签id
    double rating;//点击率

    public TagRating(String tag_id, double rating) {
        this.tag_id = tag_id;
        this.rating = rating;
    }

    public String getTag_id() {
        return tag_id;
    }

    public double getRating() {
        return rating;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
