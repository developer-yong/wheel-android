@file:Suppress("unused")

package dev.yong.wheel.base.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class SpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val endSpacing: Int = 0
) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        var orientation = LinearLayoutManager.VERTICAL
        if (parent.layoutManager is LinearLayoutManager) {
            orientation = (parent.layoutManager as LinearLayoutManager).orientation
        }
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount
        if (orientation == LinearLayoutManager.VERTICAL) {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing
            }
            if (parent.adapter != null) {
                if (parent.adapter!!.itemCount - 1 - position < spanCount) {
                    outRect.bottom = endSpacing
                }
            }
        } else {
            outRect.top = column * spacing / spanCount
            outRect.bottom = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.left = spacing
            }
            if (parent.adapter != null) {
                if (parent.adapter!!.itemCount - 1 - position < spanCount) {
                    outRect.right = endSpacing
                }
            }
        }
    }
}