package dev.yong.photo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dev.yong.photo.adapter.DirectoryAdapter;
import dev.yong.photo.adapter.PhotoAdapter;
import dev.yong.photo.bean.Directory;
import dev.yong.photo.bean.MediaFile;
import dev.yong.photo.view.MenuDialog;
import dev.yong.photo.view.SpreadListView;

/**
 * @author coderyong
 */
public class SelectorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int TAKE_PHOTO = 0x02;
    private static final int RECORD_VIDEO = 0x03;

    private TextView mBtnConfirm;
    private TextView mBtnDirectory;
    private TextView mBtnPreview;
    private CoordinatorLayout mLayoutDirectory;
    private BottomSheetBehavior mBehavior;

    private PhotoAdapter mPhotoAdapter;
    private Directory mSelectedDirectory;
    private File mPhotoFile;
    private MenuDialog mMenuDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.fullScreen(this);
        setContentView(R.layout.activity_selector);

        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnDirectory = findViewById(R.id.tv_directory);
        mBtnPreview = findViewById(R.id.tv_preview);
        mLayoutDirectory = findViewById(R.id.layout_directory);

        //ActionBar添加状态栏高度
        findViewById(R.id.action_bar).setPadding(0, Utils.getStatusBarHeight(this), 0, 0);
        //设置标题
        MediaFile.Type mediaType = PhotoSelector.getInstance().mediaType();
        String title = getString(R.string.picture_and_video);
        mBtnDirectory.setText(R.string.picture_and_video);
        if (mediaType == MediaFile.Type.IMAGE) {
            title = getString(R.string.picture);
            mBtnDirectory.setText(getString(R.string.all_picture));
        } else if (mediaType == MediaFile.Type.VIDEO) {
            title = getString(R.string.video);
            mBtnDirectory.setText(getString(R.string.all_video));
        }
        ((TextView) findViewById(R.id.tv_title)).setText(title);
        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        mLayoutDirectory.setOnClickListener(this);
        mLayoutDirectory.setClickable(false);
        mBtnDirectory.setOnClickListener(this);
        mBtnPreview.setOnClickListener(this);

        mBehavior = BottomSheetBehavior.from(findViewById(R.id.scrollView));
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                mLayoutDirectory.setClickable(newState != BottomSheetBehavior.STATE_HIDDEN);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset <= 0) {
                    int alpha = (int) (200 * (1 - Math.abs(slideOffset)));
                    mLayoutDirectory.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
                }
            }
        });
        //加载相册列表
        loadPhotoList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PhotoSelector.getInstance().isFinish()) {
            finish();
        } else {
            if (mPhotoAdapter != null) {
                mPhotoAdapter.notifyDataSetChanged();
            }
        }
    }

    private void loadPhotoList() {
        mPhotoAdapter = new PhotoAdapter(PhotoSelector.getInstance().getMediaFiles(), this);
        mPhotoAdapter.setShowCamera(PhotoSelector.getInstance().enableCamera());
        //选择监听
        mPhotoAdapter.setOnItemCheckedChangeListener((mediaFile, isChecked) -> {
            int selectedCount = PhotoSelector.getInstance().selectedCount();
            mBtnConfirm.setEnabled(selectedCount > 0);
            mBtnConfirm.setText(String.format(Locale.getDefault(),
                    getString(R.string.confirm), selectedCount, PhotoSelector.getInstance().maxSelectCount()));
            mBtnPreview.setClickable(selectedCount > 0);
            mBtnPreview.setText(selectedCount == 0 ? getString(R.string.preview) :
                    String.format(Locale.getDefault(), "%s(%d)", getString(R.string.preview), selectedCount));
        });
        //预览点击
        mPhotoAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(SelectorActivity.this, PreviewActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("directory", mSelectedDirectory);
            startActivity(intent);
        });
        //拍照点击
        mPhotoAdapter.setOnCameraClickListener(() -> {
            /*申请读取存储的权限*/
            MediaFile.Type type = PhotoSelector.getInstance().mediaType();
            if (type == MediaFile.Type.IMAGE) {
                takePhoto();
            } else if (type == MediaFile.Type.VIDEO) {
                recordVideo();
            } else {
                if (mMenuDialog == null) {
                    mMenuDialog = new MenuDialog(this);
                    mMenuDialog.setOnMenuClickListener(new MenuDialog.OnMenuClickListener() {
                        @Override
                        public void onTakePhotoClick() {
                            takePhoto();
                        }

                        @Override
                        public void onRecordVideoClick() {
                            recordVideo();
                        }
                    });
                }
                mMenuDialog.show();
            }
        });
        GridView gvImage = findViewById(R.id.gv_image);
        gvImage.setAdapter(mPhotoAdapter);

        List<Directory> directories = createDirectories();
        if (directories != null && !directories.isEmpty()) {
            mBtnDirectory.setClickable(true);
            SpreadListView lvDirectory = findViewById(R.id.lv_directory);
            DirectoryAdapter adapter = new DirectoryAdapter(directories, this);
            adapter.setOnDirectorySelectedListener(directory -> {
                mSelectedDirectory = directory;
                mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                mBtnDirectory.setText(directory.getName());
                mPhotoAdapter.replaceData(directory.getMediaFiles());
            });
            lvDirectory.setAdapter(adapter);
        } else {
            mBtnDirectory.setClickable(false);
        }
    }

    private List<Directory> createDirectories() {
        List<MediaFile> mediaFiles = PhotoSelector.getInstance().getMediaFiles();
        if (mediaFiles != null && !mediaFiles.isEmpty()) {
            Map<String, Directory> directoryMap = new HashMap<>(16);
            for (MediaFile file : mediaFiles) {
                String parent = new File(file.getPath()).getParent();
                if (!TextUtils.isEmpty(parent)) {
                    Directory directory = directoryMap.get(parent);
                    if (directory == null) {
                        directory = new Directory();
                        directory.setPic(file.getPath());
                        directory.setPath(parent);
                        directory.setMediaFiles(new ArrayList<>());
                        directoryMap.put(parent, directory);
                    }
                    directory.getMediaFiles().add(file);
                }
            }
            List<Directory> directories = new ArrayList<>(directoryMap.values());
            Directory directory = new Directory();
            directory.setPic(mediaFiles.get(0).getPath());
            MediaFile.Type mediaType = PhotoSelector.getInstance().mediaType();
            String name = getString(R.string.picture_and_video);
            if (mediaType == MediaFile.Type.IMAGE) {
                name = getString(R.string.all_picture);
            } else if (mediaType == MediaFile.Type.VIDEO) {
                name = getString(R.string.all_video);
            }
            directory.setSelected(true);
            directory.setName(name);
            directory.setMediaFiles(mediaFiles);
            directories.add(0, directory);
            return directories;
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            onBackPressed();
        } else if (id == R.id.layout_directory) {
            mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if (id == R.id.tv_directory) {
            if (mBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        } else if (id == R.id.tv_preview) {
            if (PhotoSelector.getInstance().selectedCount() > 0) {
                Intent intent = new Intent(this, PreviewActivity.class);
                intent.putExtra("isPreviewSelected", true);
                startActivity(intent);
            }
        } else if (id == R.id.btn_confirm) {
            onBackPressed();
            PhotoSelector.getInstance().finish();
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {

        //打开相机的Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断系统是否可以打开相机
        if (intent.resolveActivity(getPackageManager()) != null) {
            String imageFileName = "IMG_" + new SimpleDateFormat(
                    "yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
            mPhotoFile = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM), imageFileName);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                /*7.0以上要通过FileProvider将File转化为Uri*/
                uri = FileProvider.getUriForFile(this, "dev.yong.photo.fileprovider", mPhotoFile);
            } else {
                /*7.0以下则直接使用Uri的fromFile方法将File转化为Uri*/
                uri = Uri.fromFile(mPhotoFile);
            }
            //将用于输出的文件Uri传递给相机
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            startActivityForResult(intent, TAKE_PHOTO);
        }
    }

    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            //好使
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, PhotoSelector.getInstance().maxFileSize());
            startActivityForResult(intent, RECORD_VIDEO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            File file = null;
            if (requestCode == TAKE_PHOTO) {
                if (mPhotoFile != null && mPhotoFile.exists()) {
                    file = mPhotoFile;
                }
            } else {
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        Log.e("TAG", "onActivityResult: " + uri.toString());
                        String[] projection = {MediaStore.MediaColumns.DATA};

                        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

                        if (cursor != null) {
                            cursor.moveToFirst();
                            file = new File(cursor.getString(cursor.getColumnIndex(projection[0])));
                            cursor.close();
                        }
                    }
                }
            }
            if (file != null) {
                MediaFile mediaFile = new MediaFile();
                mediaFile.setType(requestCode == TAKE_PHOTO ? MediaFile.Type.IMAGE : MediaFile.Type.VIDEO);
                mediaFile.setName(file.getName());
                mediaFile.setPath(file.getAbsolutePath());
                mediaFile.setSize(file.length());
                mediaFile.setLastModified(file.lastModified());
                mediaFile.setSelected(true);
                mPhotoAdapter.addData(mediaFile);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhotoSelector.getInstance().cancel();
    }
}
