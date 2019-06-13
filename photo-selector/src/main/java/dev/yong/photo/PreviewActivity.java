package dev.yong.photo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
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

    private TextView mTvTitle;
    private TextView mBtnConfirm;
    private RadioButton mBtnOriginal;
    private CheckBox mCbSelect;
    private ViewPager mPager;

    private boolean isOriginalImage = false;
    private boolean isPreviewSelected = false;
    private int currentPosition = 0;
    private List<MediaFile> mediaFiles;

    public PreviewActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Intent intent = getIntent();

        isPreviewSelected = intent.getBooleanExtra("isPreviewSelected", isPreviewSelected);
        currentPosition = intent.getIntExtra("position", currentPosition);
        List<MediaFile> selectedMediaFiles = PhotoSelector.getInstance().getSelectedMediaFiles();
        mediaFiles = isPreviewSelected ? selectedMediaFiles : PhotoSelector.getInstance().getDirMediaFiles();

        mTvTitle = findViewById(R.id.tv_title);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnOriginal = findViewById(R.id.rb_original);
        mCbSelect = (CheckBox) findViewById(R.id.cb_select);
        mPager = (ViewPager) findViewById(R.id.pager);

        findViewById(R.id.btn_back).setOnClickListener(this);
        mTvTitle.setText(String.format(Locale.getDefault(), "%d/%d", currentPosition + 1, mediaFiles.size()));
        mBtnOriginal.setVisibility(PhotoSelector.getInstance().isCompress() ? View.VISIBLE : View.GONE);
        mBtnConfirm.setOnClickListener(this);
        mBtnOriginal.setOnClickListener(this);
        mCbSelect.setOnClickListener(this);

        if (mediaFiles == null) {
            mediaFiles = new ArrayList<>();
        }
        mCbSelect.setChecked(mediaFiles.get(currentPosition).isSelected());
        mPager.setAdapter(new PreviewPageAdapter(mediaFiles));
        mPager.setCurrentItem(currentPosition);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCbSelect.setChecked(mediaFiles.get(position).isSelected());
                mTvTitle.setText(String.format(Locale.getDefault(), "%d/%d", position + 1, mediaFiles.size()));
            }
        });

        PhotoSelector.getInstance().setOnSelectCountListener(new PhotoSelector.OnSelectCountListener() {
            @Override
            public void onCount(int selectCount) {
                mBtnConfirm.setEnabled(selectCount > 0);
                mBtnConfirm.setText(String.format(Locale.getDefault(),
                        "确定(%d/%d)", selectCount, PhotoSelector.getInstance().maxSelectCount()));
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            onBackPressed();
        } else if (id == R.id.rb_original) {
            isOriginalImage = !isOriginalImage;
            mBtnOriginal.setChecked(isOriginalImage);
        } else if (id == R.id.cb_select) {
            if (mCbSelect.isChecked()) {
                if (!PhotoSelector.getInstance().addSelected(mediaFiles.get(mPager.getCurrentItem()))) {
                    mCbSelect.setChecked(false);
                }
            } else {
                PhotoSelector.getInstance().removeSelected(mediaFiles.get(mPager.getCurrentItem()));
            }
        } else if (id == R.id.btn_confirm) {
            PhotoSelector.getInstance().onSelectConfirm(this);
        }
    }
}
