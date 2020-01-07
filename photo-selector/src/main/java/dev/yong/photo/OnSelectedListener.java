package dev.yong.photo;

import java.util.List;

public interface OnSelectedListener {
    /**
     * 选择确认时被调用
     *
     * @param selectPaths 用户所选择的文件集合
     */
    void onSelected(List<String> selectPaths);
}