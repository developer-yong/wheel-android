package dev.yong.photo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.yong.photo.adapter.PreviewPageAdapter;
import dev.yong.photo.bean.Directory;
import dev.yong.photo.bean.MediaFile;


/**
 * @author coderyong
 */
public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PHOTO_CROP = 0x03;

    private FrameLayout mActionBar;
    private ConstraintLayout mLayoutBottom;
    private TextView mTvTitle;
    private TextView mBtnConfirm;
    private RadioButton mBtnOriginal;
    private CheckBox mCbSelect;
    private ViewPager mPager;

    private boolean isPreviewSelected = false;
    private int mCurrentPosition = 0;
    private List<MediaFile> mMediaFiles;
    private PreviewPageAdapter mPageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.fullScreen(this);
        setContentView(R.layout.activity_preview);

        mActionBar = findViewById(R.id.action_bar);
        mLayoutBottom = findViewById(R.id.layout_bottom);
        mTvTitle = findViewById(R.id.tv_title);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnOriginal = findViewById(R.id.rb_original);
        mCbSelect = findViewById(R.id.cb_select);
        mPager = findViewById(R.id.pager);

        //ActionBar添加状态栏高度
        mActionBar.setPadding(0, Utils.getStatusBarHeight(this), 0, 0);
        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        mBtnOriginal.setOnClickListener(this);
        mCbSelect.setOnClickListener(this);

        initMediaFiles();

        mTvTitle.setText(String.format(Locale.getDefault(), "%d/%d", mCurrentPosition + 1, mMediaFiles.size()));
        int selectedCount = PhotoSelector.getInstance().selectedCount();
        mBtnConfirm.setEnabled(selectedCount > 0);
        mBtnConfirm.setText(String.format(Locale.getDefault(),
                "确定(%d/%d)", selectedCount, PhotoSelector.getInstance().maxSelectCount()));
        mBtnOriginal.setVisibility(PhotoSelector.getInstance().enableCompress() ? View.VISIBLE : View.GONE);
        mBtnOriginal.setChecked(!mMediaFiles.get(mCurrentPosition).isCompress());
        mCbSelect.setChecked(mMediaFiles.get(mCurrentPosition).isSelected());
        mPageAdapter = new PreviewPageAdapter(mMediaFiles);
        mPager.setAdapter(mPageAdapter);
        mPager.setCurrentItem(mCurrentPosition);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mBtnOriginal.setChecked(!mMediaFiles.get(position).isCompress());
                mCbSelect.setChecked(mMediaFiles.get(position).isSelected());
                mTvTitle.setText(String.format(Locale.getDefault(), "%d/%d", position + 1, mMediaFiles.size()));
            }
        });

        mPageAdapter.setOnItemClickListener(position -> showAction());
    }

    private void initMediaFiles() {
        Intent intent = getIntent();
        isPreviewSelected = intent.getBooleanExtra("isPreviewSelected", isPreviewSelected);
        mCurrentPosition = intent.getIntExtra("position", mCurrentPosition);
        Directory directory = (Directory) intent.getSerializableExtra("directory");
        if (isPreviewSelected) {
            if (mMediaFiles == null) {
                mMediaFiles = new ArrayList<>();
            }
            for (MediaFile mediaFile : PhotoSelector.getInstance().getMediaFiles()) {
                if (mediaFile.isSelected()) {
                    mMediaFiles.add(mediaFile);
                }
            }
        } else if (directory != null) {
            mMediaFiles = directory.getMediaFiles();
        } else {
            mMediaFiles = PhotoSelector.getInstance().getMediaFiles();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            onBackPressed();
        } else if (id == R.id.rb_original) {
            mMediaFiles.get(mPager.getCurrentItem()).setCompress(!mBtnOriginal.isChecked());
        } else if (id == R.id.cb_select) {
            if (mCbSelect.isChecked()) {
                int maxCount = PhotoSelector.getInstance().maxSelectCount();
                if (PhotoSelector.getInstance().selectedCount() == maxCount) {
                    mCbSelect.setChecked(false);
                    Toast.makeText(this, "您最多只能选择" + maxCount + "个", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            mMediaFiles.get(mPager.getCurrentItem()).setSelected(mCbSelect.isChecked());
            int selectedCount = PhotoSelector.getInstance().selectedCount();
            mBtnConfirm.setEnabled(selectedCount > 0);
            mBtnConfirm.setText(String.format(Locale.getDefault(),
                    "确定(%d/%d)", selectedCount, PhotoSelector.getInstance().maxSelectCount()));
        } else if (id == R.id.btn_confirm) {
            PhotoSelector.getInstance().finish();
            onBackPressed();
        }
    }

    private void photoCrop(String photoPath) {
        // 调用系统中自带的图片剪裁
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(Uri.parse(photoPath), "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_CROP);
    }

    private void showAction() {
        int visibility = mActionBar.getVisibility();
        mActionBar.startAnimation(AnimationUtils.loadAnimation(this,
                visibility == View.VISIBLE ? R.anim.bottom_to_top_hide : R.anim.top_to_bottom_show));
        mLayoutBottom.startAnimation(AnimationUtils.loadAnimation(this,
                visibility == View.VISIBLE ? R.anim.top_to_bottom_hide : R.anim.bottom_to_top_show));
        mActionBar.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
        mLayoutBottom.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
        if (visibility == View.VISIBLE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}
