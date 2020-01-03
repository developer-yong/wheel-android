package dev.yong.photo.compress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.yong.photo.bean.MediaFile;
import top.zibin.luban.Luban;

public class DefaultCompressFactory implements CompressFactory {

    private Context mContext;
    private CompressCompleteListener mListener;

    @Override
    public void compress(Context context, List<MediaFile> mediaFiles, CompressCompleteListener listener) {
        mContext = context;
        mListener = listener;
        new CompressTask().execute(mediaFiles.toArray(new MediaFile[]{}));
    }

    @SuppressLint("StaticFieldLeak")
    class CompressTask extends AsyncTask<MediaFile, Integer, List<String>> {

        @Override
        protected List<String> doInBackground(MediaFile... mediaFiles) {
            List<String> selectPaths = new ArrayList<>();
            List<String> imageFiles = new ArrayList<>();
            List<String> videoFiles = new ArrayList<>();

            for (MediaFile mediaFile : mediaFiles) {
                if (mediaFile.getType() == MediaFile.Type.IMAGE) {
                    imageFiles.add(mediaFile.getPath());
                } else {
                    videoFiles.add(mediaFile.getPath());
                }
            }
            try {
                List<File> files = Luban.with(mContext)
                        .ignoreBy(100)
                        .load(imageFiles)
                        .filter(path -> !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif")))
                        .get();
                for (File file : files) {
                    selectPaths.add(file.getAbsolutePath());
                }
            } catch (IOException e) {
                selectPaths.addAll(imageFiles);
            }
            selectPaths.addAll(videoFiles);
            return selectPaths;
        }

        @Override
        protected void onPostExecute(List<String> results) {
            super.onPostExecute(results);
            if (mListener != null) {
                mListener.compress(results);
            }
        }
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return 是否删除
     */
    private boolean delete(File file) {
        if (file != null && file.exists()) {
            if (file.isFile()) {
                return file.delete();
            } else if (file.isDirectory()) {
                //声明目录下所有的文件 files[];
                File[] files = file.listFiles();
                if (files != null) {
                    //遍历目录下所有的文件
                    for (File f : files) {
                        //把每个文件用这个方法进行迭代
                        delete(f);
                    }
                }
                //删除文件夹
                return file.delete();
            }
        }
        return true;
    }
}
