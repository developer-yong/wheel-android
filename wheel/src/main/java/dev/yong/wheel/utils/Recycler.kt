@file:Suppress("unused")

package dev.yong.wheel.utils

import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import kotlin.math.ceil

/**
 * @author coderyong
 */
object Recycler {

    @JvmStatic
    fun with(recyclerView: RecyclerView): Builder {
        return Builder(recyclerView)
    }

    class Builder(
        private val mRecyclerView: RecyclerView,
    ) {
        /**
         * RecyclerView.Adapter
         */
        private var mAdapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>? = null

        /**
         * LayoutManager
         * <P>默认为LinearLayoutManager</P>
         */
        private var mLayoutManager: RecyclerView.LayoutManager? = null

        /**
         * 是否使用ItemDecoration，默认为false
         */
        private var useItemDecoration = false

        /**
         * Item分割线
         * <P>默认DividerItemDecoration垂直方向</P>
         */
        private var mItemDecoration: ItemDecoration? = null

        /**
         * 创建分割线Drawable
         */
        private var mDividerDrawable: Drawable? = null

        fun setAdapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>): Builder {
            mAdapter = adapter
            return this
        }

        fun setLayoutManager(layoutManager: RecyclerView.LayoutManager?): Builder {
            mLayoutManager = layoutManager
            return this
        }

        fun useItemDecoration(): Builder {
            useItemDecoration = true
            return this
        }

        fun setItemDecoration(itemDecoration: ItemDecoration?): Builder {
            useItemDecoration = true
            mItemDecoration = itemDecoration
            return this
        }

        fun setDividerDrawable(dividerDrawable: Drawable?): Builder {
            useItemDecoration = true
            mDividerDrawable = dividerDrawable
            return this
        }

        inline fun <reified A : RecyclerView.Adapter<out RecyclerView.ViewHolder>> build(): A {
            return build(A::class.java)
        }

        @Suppress("UNCHECKED_CAST")
        fun <A : RecyclerView.Adapter<out RecyclerView.ViewHolder>> build(aClazz: Class<A>): A {
            if (useItemDecoration) {
                if (mItemDecoration == null) {
                    mItemDecoration = DividerItemDecoration(
                        mRecyclerView.context, DividerItemDecoration.VERTICAL
                    )
                }
                if (mDividerDrawable != null) {
                    if (mItemDecoration is DividerItemDecoration) {
                        (mItemDecoration as DividerItemDecoration).setDrawable(mDividerDrawable!!)
                    }
                }
                mRecyclerView.addItemDecoration(mItemDecoration!!)
            }
            if (mRecyclerView.layoutManager == null) {
                if (mLayoutManager == null) {
                    mLayoutManager = GridLayoutManager(mRecyclerView.context, 1)
                }
                //设置LayoutManager
                mRecyclerView.layoutManager = mLayoutManager
            }

            //设置Adapter
            if (mAdapter == null) {
                mAdapter = aClazz.newInstance()
            }
            mRecyclerView.adapter = mAdapter
            return (mAdapter as A?)!!
        }
    }
}

/**
 * 计算分页页码
 *
 * @param pageSize 分页大小
 * @return 下一页的分页页码
 */
fun calculatePageNumber(totalSize: Float, pageSize: Float): Int {
    //向上取整并加1
    return ceil((totalSize / pageSize).toDouble()).toInt() + 1
}