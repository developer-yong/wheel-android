package dev.yong.photo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.yong.photo.adapter.PreviewPageAdapter;
import dev.yong.photo.bean.MediaFile;


/**
 * @author coderyong
 */
public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private FrameLayout mActionBar;
    private ConstraintLayout mLayoutBottom;
    private TextView mTvTitle;
    private TextView mBtnConfirm;
    private RadioButton mBtnOriginal;
    private CheckBox mCbSelect;
    private ViewPager mPager;

    private boolean isOriginalImage = false;
    private boolean isPreviewSelected = false;
    private int mCurrentPosition = 0;
    private List<MediaFile> mMediaFiles;
    private PreviewPageAdapter mPageAdapter;

    public PreviewActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_preview);

        Intent intent = getIntent();

        isPreviewSelected = intent.getBooleanExtra("isPreviewSelected", isPreviewSelected);
        mCurrentPosition = intent.getIntExtra("position", mCurrentPosition);
        List<MediaFile> selectedMediaFiles = PhotoSelector.getInstance().getSelectedMediaFiles();
        mMediaFiles = isPreviewSelected ? selectedMediaFiles : PhotoSelector.getInstance().getDirMediaFiles();

        mActionBar = findViewById(R.id.action_bar);
        mLayoutBottom = findViewById(R.id.layout_bottom);
        mTvTitle = findViewById(R.id.tv_title);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnOriginal = findViewById(R.id.rb_original);
        mCbSelect = findViewById(R.id.cb_select);
        mPager = findViewById(R.id.pager);

        findViewById(R.id.btn_back).setOnClickListener(this);
        mTvTitle.setText(String.format(Locale.getDefault(), "%d/%d", mCurrentPosition + 1, mMediaFiles.size()));
        mBtnOriginal.setVisibility(PhotoSelector.getInstance().isCompress() ? View.VISIBLE : View.GONE);
        mBtnConfirm.setOnClickListener(this);
        mBtnOriginal.setOnClickListener(this);
        mCbSelect.setOnClickListener(this);
        //ActionBar添加状态栏高度
        mActionBar.setPadding(0, getStatusBarHeight(), 0, 0);
        mBtnOriginal.setVisibility(PhotoSelector.getInstance().isSupportCompress() ? View.VISIBLE : View.GONE);
        mBtnOriginal.setChecked(!PhotoSelector.getInstance().isCompress());

        if (mMediaFiles == null) {
            mMediaFiles = new ArrayList<>();
        }
        mCbSelect.setChecked(mMediaFiles.get(mCurrentPosition).isSelected());
        mPageAdapter = new PreviewPageAdapter(mMediaFiles);
        mPageAdapter.setMediaController(new MediaController(this));
        mPager.setAdapter(mPageAdapter);
        mPager.setCurrentItem(mCurrentPosition);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mPageAdapter.stopVideo();
                mCbSelect.setChecked(mMediaFiles.get(position).isSelected());
                mTvTitle.setText(String.format(Locale.getDefault(), "%d/%d", position + 1, mMediaFiles.size()));
            }
        });

        PhotoSelector.getInstance().setOnSelectCountListener(selectCount -> {
            mBtnConfirm.setEnabled(selectCount > 0);
            mBtnConfirm.setText(String.format(Locale.getDefault(),
                    "确定(%d/%d)", selectCount, PhotoSelector.getInstance().maxSelectCount()));
        });
        mPageAdapter.setOnItemClickListener(position -> showAction());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            onBackPressed();
        } else if (id == R.id.rb_original) {
            isOriginalImage = !isOriginalImage;
            mBtnOriginal.setChecked(isOriginalImage);
            PhotoSelector.getInstance().configCompress(!isOriginalImage);
        } else if (id == R.id.cb_select) {
            if (mCbSelect.isChecked()) {
                if (!PhotoSelector.getInstance().addSelected(mMediaFiles.get(mPager.getCurrentItem()))) {
                    mCbSelect.setChecked(false);
                }
            } else {
                PhotoSelector.getInstance().removeSelected(mMediaFiles.get(mPager.getCurrentItem()));
            }
        } else if (id == R.id.btn_confirm) {
            PhotoSelector.getInstance().onSelectConfirm();
            onBackPressed();
        }
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

    /**
     * 获取状态栏高度
     */
    public int getStatusBarHeight() {
        // 获得状态栏高度
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return getResources().getDimensionPixelSize(resourceId);
    }

    @Override
    public void onBackPressed() {
        if (mPageAdapter != null) {
            mPageAdapter.stopVideo();
        }
        super.onBackPressed();
    }
}
