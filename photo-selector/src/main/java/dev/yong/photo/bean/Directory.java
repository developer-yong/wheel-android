package dev.yong.photo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author coderyong
 */
public class Directory implements Serializable {

    private String pic;
    private String name;
    private String path;
    private boolean isSelected;
    private List<MediaFile> mediaFiles;

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
        setName(path.substring(path.lastIndexOf("/") + 1));
        this.path = path;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public List<MediaFile> getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(List<MediaFile> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }
}
