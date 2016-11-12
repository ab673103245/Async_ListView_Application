package qianfeng.async_listview_application;

/**
 * Created by Administrator on 2016/8/31 0031.
 */
public class Food {

    private String description;
    private String keywords;
    private String img;

    public Food(String description, String keywords, String img) {
        this.description = description;
        this.keywords = keywords;
        this.img = img;
    }

    public Food() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "Food{" +
                "description='" + description + '\'' +
                ", keywords='" + keywords + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
