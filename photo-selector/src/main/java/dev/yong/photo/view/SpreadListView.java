package dev.yong.photo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author coderyong
 */
public class SpreadListView extends ListView {

    public SpreadListView(Context context) {
        this(context, null);
    }

    public SpreadListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpreadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
