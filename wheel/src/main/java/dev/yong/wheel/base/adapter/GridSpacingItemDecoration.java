package dev.yong.wheel.base.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.RecyclerView.ItemDecoration;

public class GridSpacingItemDecoration extends ItemDecoration {

    private final int spanCount;
    private final int spacing;
    private final boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % this.spanCount;
        if (this.includeEdge) {
            outRect.left = this.spacing - column * this.spacing / this.spanCount;
            outRect.right = (column + 1) * this.spacing / this.spanCount;
            if (position < this.spanCount) {
                outRect.top = this.spacing;
            }

            outRect.bottom = this.spacing;
        } else {
            outRect.left = column * this.spacing / this.spanCount;
            outRect.right = this.spacing - (column + 1) * this.spacing / this.spanCount;
            if (position < this.spanCount) {
                outRect.top = this.spacing;
            }

            outRect.bottom = this.spacing;
        }
    }
}
