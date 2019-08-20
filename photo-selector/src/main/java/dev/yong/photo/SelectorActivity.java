package dev.yong.photo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dev.yong.photo.adapter.DirectoryAdapter;
import dev.yong.photo.adapter.PhotoAdapter;
import dev.yong.photo.bean.Directory;
import dev.yong.photo.bean.MediaFile;
import dev.yong.photo.view.SpreadListView;

/**
 * @author coderyong
 */
public class SelectorActivity extends AppCompatActivity implements View.OnClickListener, PhotoSelector.OnSelectCountListener, PhotoAdapter.OnCameraClickListener {

    private static final int PERMISSION_CODE = 0x01;
    private static final int TAKE_PHOTO = 0x02;

    private TextView mBtnConfirm;
    private TextView mBtnDirectory;
    private RadioButton mBtnOriginal;
    private TextView mBtnPreview;
    private CoordinatorLayout mLayoutDirectory;
    private BottomSheetBehavior mBehavior;

    private PhotoAdapter mPhotoAdapter;

    private boolean isOriginalImage = false;

    private File mPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //4.4 全透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        //5.0 全透明实现
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.color_action_bar));
        }
        setContentView(R.layout.activity_selector);

        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnDirectory = findViewById(R.id.tv_directory);
        mBtnOriginal = findViewById(R.id.rb_original);
        mBtnOriginal.setVisibility(PhotoSelector.getInstance().isCompress() ? View.VISIBLE : View.GONE);
        mBtnPreview = findViewById(R.id.tv_preview);
        mLayoutDirectory = findViewById(R.id.layout_directory);

        //ActionBar添加状态栏高度
        findViewById(R.id.action_bar).setPadding(0, getStatusBarHeight(), 0, 0);
        //设置标题
        String title = getString(R.string.picture_and_video);
        mBtnDirectory.setText(R.string.picture_and_video);
        if (PhotoSelector.getInstance().mediaType() == MediaFile.Type.IMAGE) {
            title = getString(R.string.picture);
            mBtnDirectory.setText(getString(R.string.all_picture));
        } else if (PhotoSelector.getInstance().mediaType() == MediaFile.Type.VIDEO) {
            title = getString(R.string.video);
            mBtnDirectory.setText(getString(R.string.all_video));
        }
        ((TextView) findViewById(R.id.tv_title)).setText(title);
        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        mLayoutDirectory.setOnClickListener(this);
        mLayoutDirectory.setClickable(false);
        mBtnDirectory.setOnClickListener(this);
        mBtnOriginal.setOnClickListener(this);
        mBtnPreview.setOnClickListener(this);

        mBtnOriginal.setVisibility(PhotoSelector.getInstance().isSupportCompress() ? View.VISIBLE : View.GONE);
        mBtnOriginal.setChecked(!PhotoSelector.getInstance().isCompress());

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
            PhotoSelector.getInstance().setOnSelectCountListener(this);
            if (mPhotoAdapter != null) {
                mPhotoAdapter.notifyDataSetChanged();
            }
        }
    }

    private void loadPhotoList() {
        mPhotoAdapter = new PhotoAdapter(PhotoSelector.getInstance().getMediaFiles(), this);
        mPhotoAdapter.setShowCamera(PhotoSelector.getInstance().enableCamera());
        mPhotoAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(SelectorActivity.this, PreviewActivity.class);
            intent.putExtra("position", position);
            startActivity(intent);
        });
        mPhotoAdapter.setOnCameraClickListener(this);
        GridView gvImage = findViewById(R.id.gv_image);
        gvImage.setAdapter(mPhotoAdapter);

        List<Directory> directories = PhotoSelector.getInstance().getDirectories();
        if (directories != null && !directories.isEmpty()) {
            mBtnDirectory.setClickable(true);
            SpreadListView lvDirectory = findViewById(R.id.lv_directory);
            DirectoryAdapter adapter = new DirectoryAdapter(directories, this);
            adapter.setOnDirectorySelectedListener(directory -> {
                mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                mBtnDirectory.setText(directory.getName());
                PhotoSelector.getInstance().setParentDir(directory.getPath());
                mPhotoAdapter.replaceData(PhotoSelector.getInstance().getDirMediaFiles());
            });
            lvDirectory.setAdapter(adapter);
        } else {
            mBtnDirectory.setClickable(false);
        }
    }

    @Override
    public void onCount(int selectCount) {
        mBtnConfirm.setEnabled(selectCount > 0);
        mBtnConfirm.setText(String.format(Locale.getDefault(),
                getString(R.string.confirm), selectCount, PhotoSelector.getInstance().maxSelectCount()));
        mBtnPreview.setClickable(selectCount > 0);
        mBtnPreview.setText(selectCount == 0 ? getString(R.string.preview) :
                String.format(Locale.getDefault(), "%s(%d)", getString(R.string.preview), selectCount));
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
        } else if (id == R.id.rb_original) {
            isOriginalImage = !isOriginalImage;
            mBtnOriginal.setChecked(isOriginalImage);
        } else if (id == R.id.tv_preview) {
            if (!PhotoSelector.getInstance().getSelectedMediaFiles().isEmpty()) {
                Intent intent = new Intent(this, PreviewActivity.class);
                intent.putExtra("isPreviewSelected", true);
                startActivity(intent);
            }
        } else if (id == R.id.btn_confirm) {
            onBackPressed();
            PhotoSelector.getInstance().onSelectConfirm();
        }
    }

    @Override
    public void onCameraClick() {
        /*申请读取存储的权限*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
            } else {
                takePhoto();
            }
        } else {
            takePhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
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
            String imageFileName = "JPEG_" + new SimpleDateFormat(
                    "yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + "_";
            try {
                mPhotoFile = File.createTempFile(imageFileName, ".jpg",
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mPhotoFile != null) {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TAKE_PHOTO) {
            if (mPhotoFile != null && mPhotoFile.exists()) {
                MediaFile mediaFile = new MediaFile();
                mediaFile.setType(MediaFile.Type.IMAGE);
                mediaFile.setName(mPhotoFile.getName());
                mediaFile.setPath(mPhotoFile.getAbsolutePath());
                mediaFile.setSize(mPhotoFile.length());
                mediaFile.setLastModified(mPhotoFile.lastModified());
                PhotoSelector.getInstance().addSelected(mediaFile);
                onBackPressed();
                PhotoSelector.getInstance().onSelectConfirm();
            }
        }
    }

    /**
     * 获取状态栏高度
     */
    public int getStatusBarHeight() {
        // 获得状态栏高度
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return getResources().getDimensionPixelSize(resourceId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhotoSelector.getInstance().reset();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
