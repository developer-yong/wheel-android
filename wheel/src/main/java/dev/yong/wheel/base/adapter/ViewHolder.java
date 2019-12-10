package dev.yong.wheel.base.adapter;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author CoderYong
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    /**
     * Views indexed with their IDs
     */
    private final SparseArray<View> mViews;
    protected View mView;

    public ViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
        mView = itemView;
    }

    /**
     * 通过ViewId获取View
     *
     * @param viewId viewId
     * @return view
     */
    public <V extends View> V get(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (V) view;
    }

    /**
     * 获取TextView
     *
     * @param viewId TextView 资源id
     * @return TextView
     */
    public TextView text(@IdRes int viewId) {
        return get(viewId);
    }

    /**
     * TextView 添加Links
     * {@link Linkify#addLinks(TextView, int)}
     *
     * @param viewId TextView 资源id
     * @return ViewHolder
     */
    public ViewHolder addLinks(@IdRes int viewId) {
        Linkify.addLinks((TextView) get(viewId), Linkify.ALL);
        return this;
    }

    /**
     * TextView 设置Typeface
     *
     * @param viewId   TextView 资源id
     * @param typeface {@link TextView#setTypeface(Typeface)}
     * @return ViewHolder
     */
    public ViewHolder setTypeface(@IdRes int viewId, Typeface typeface) {
        TextView text = get(viewId);
        text.setTypeface(typeface);
        text.setPaintFlags(text.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        return this;
    }

    /**
     * 获取EditText
     *
     * @param viewId EditText 资源id
     * @return EditText
     */
    public EditText edit(@IdRes int viewId) {
        return get(viewId);
    }

    /**
     * 获取ImageView
     *
     * @param viewId ImageView 资源id
     * @return ImageView
     */
    public ImageView image(@IdRes int viewId) {
        return get(viewId);
    }

    /**
     * 获取ProgressBar
     *
     * @param viewId ProgressBar 资源id
     * @return ProgressBar
     */
    public ProgressBar progress(@IdRes int viewId) {
        return get(viewId);
    }

    /**
     * ProgressBar 设置进度
     *
     * @param viewId   ProgressBar 资源id
     * @param progress progress
     * @param max      最大值
     * @return ViewHolder
     */
    public ViewHolder setProgress(@IdRes int viewId, int progress, int max) {
        ProgressBar view = get(viewId);
        view.setMax(max);
        view.setProgress(progress);
        return this;
    }

    /**
     * 获取RatingBar
     *
     * @param viewId RatingBar 资源id
     * @return RatingBar
     */
    public RatingBar rating(@IdRes int viewId, float rating) {
        return get(viewId);
    }

    /**
     * RatingBar 设置rating
     *
     * @param viewId RatingBar 资源id
     * @param rating rating
     * @param max    最大值
     * @return ViewHolder
     */
    public ViewHolder setRating(@IdRes int viewId, float rating, int max) {
        RatingBar view = get(viewId);
        view.setMax(max);
        view.setRating(rating);
        return this;
    }

    /**
     * 获取AdapterView
     *
     * @param viewId AdapterView 资源id
     * @return AdapterView
     */
    public AdapterView adapterView(@IdRes int viewId) {
        return get(viewId);
    }

    /**
     * 获取CompoundButton
     *
     * @param viewId CompoundButton 资源id
     * @return CompoundButton
     */
    public CompoundButton compoundButton(@IdRes int viewId) {
        return get(viewId);
    }

    /**
     * View 设置Checked
     *
     * @param viewId  Checkable 资源id
     * @param checked checked
     * @return ViewHolder
     */
    public ViewHolder setChecked(@IdRes int viewId, boolean checked) {
        View view = get(viewId);
        // View unable cast to Checkable
        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(checked);
        }
        return this;
    }
}
