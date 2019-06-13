package dev.yong.photo.bean;

/**
 * @author coderyong
 */
public class Directory {

    private String pic;
    private String name;
    private String path;
    private int number;
    private boolean isSelected;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        setName(path.substring(path.lastIndexOf("/") + 1, path.length()));
        this.path = path;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
