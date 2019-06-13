package dev.yong.photo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;

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
public class SelectorActivity extends AppCompatActivity implements View.OnClickListener, PhotoSelector.OnSelectCountListener {

    private TextView mBtnConfirm;
    private TextView mBtnDirectory;
    private RadioButton mBtnOriginal;
    private TextView mBtnPreview;
    private CoordinatorLayout mLayoutDirectory;
    private BottomSheetBehavior mBehavior;

    private List<Directory> mDirectories;
    private PhotoAdapter mPhotoAdapter;

    private boolean isOriginalImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);

        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnDirectory = findViewById(R.id.tv_directory);
        mBtnOriginal = findViewById(R.id.rb_original);
        mBtnOriginal.setVisibility(PhotoSelector.getInstance().isCompress() ? View.VISIBLE : View.GONE);
        mBtnPreview = findViewById(R.id.tv_preview);
        mLayoutDirectory = findViewById(R.id.layout_directory);

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
        PhotoSelector.getInstance().setOnSelectCountListener(this);
        if (mPhotoAdapter != null) {
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    private void loadPhotoList() {
        mPhotoAdapter = new PhotoAdapter(PhotoSelector.getInstance().getMediaFiles(), this);
        mPhotoAdapter.setShowCamera(PhotoSelector.getInstance().enableCamera());
        mPhotoAdapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(SelectorActivity.this, PreviewActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        GridView gvImage = findViewById(R.id.gv_image);
        gvImage.setAdapter(mPhotoAdapter);

        List<Directory> directories = PhotoSelector.getInstance().getDirectories();
        if (directories != null && !directories.isEmpty()) {
            mBtnDirectory.setClickable(true);
            SpreadListView lvDirectory = findViewById(R.id.lv_directory);
            DirectoryAdapter adapter = new DirectoryAdapter(directories, this);
            adapter.setOnDirectorySelectedListener(new DirectoryAdapter.OnDirectorySelectedListener() {
                @Override
                public void onSelected(Directory directory) {
                    mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    mBtnDirectory.setText(directory.getName());
                    PhotoSelector.getInstance().setParentDir(directory.getPath());
                    mPhotoAdapter.replaceData(PhotoSelector.getInstance().getDirMediaFiles());
                }
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
                "确定(%d/%d)", selectCount, PhotoSelector.getInstance().maxSelectCount()));
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
            PhotoSelector.getInstance().onSelectConfirm(this);
        }
    }

    @Override
    public void onBackPressed() {
        PhotoSelector.getInstance().reset();
        super.onBackPressed();
    }
}
