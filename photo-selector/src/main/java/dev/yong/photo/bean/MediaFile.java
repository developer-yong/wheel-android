package dev.yong.photo.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author coderyong
 */
public class MediaFile implements Serializable {

    /**
     * 文件ID
     */
    private int id;
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 压缩文件路径
     */
    private String compressPath;
    /**
     * 文件日期
     */
    private String date;
    /**
     * 时长
     */
    private String duration;
    /**
     * 文件最后更新时间
     */
    private long lastModified;
    /**
     * 文件大小
     */
    private long size;
    /**
     * 文件类型
     */
    private Type type;
    /**
     * 是否被选中
     */
    private boolean isSelected = false;

    private boolean isCompress = true;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        this.path = path;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss", Locale.getDefault());
        String time = "";
        if (duration < 60000 * 60) {
            time = format.format(duration);
        } else {
            long minute = duration / 60000;
            long second = Math.round((float) duration % 60000 / 1000);
            time += minute + ":";
            if (second < 10) {
                time += "0";
            }
            time += second;
        }
        this.duration = time;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        if (lastModified + "".length() >= 13) {
            lastModified /= 1000;
        }
        this.lastModified = lastModified;
        setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(lastModified));
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isCompress() {
        return isCompress;
    }

    public void setCompress(boolean compress) {
        isCompress = compress;
    }

    public enum Type {
        /**
         * 图片文件，视频文件
         */
        IMAGE, VIDEO
    }
}
