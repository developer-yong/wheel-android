package dev.yong.photo.compress;

import java.util.List;

/**
 * @author coderyong
 */
public interface CompressCompleteListener {

    /**
     * 压缩文件
     *
     * @param selectPaths 文件集合路径集合
     */
    void compress(List<String> selectPaths);

}
