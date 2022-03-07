@file:Suppress("unused")

package dev.yong.wheel.base.adapter

import android.graphics.Paint
import android.graphics.Typeface
import android.text.util.Linkify
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * 通过ViewId获取View
 *
 * @param viewId viewId
 * @return view
 */
fun <V : View> RecyclerView.ViewHolder.get(@IdRes viewId: Int): V {
    return itemView.findViewById(viewId) as V
}

/**
 * 获取TextView
 *
 * @param viewId TextView 资源id
 * @return TextView
 */
fun RecyclerView.ViewHolder.text(@IdRes viewId: Int): TextView {
    return get(viewId)
}

/**
 * TextView 添加Links
 * [Linkify.addLinks]
 *
 * @param viewId TextView 资源id
 * @return ViewHolder
 */
fun RecyclerView.ViewHolder.addLinks(@IdRes viewId: Int): RecyclerView.ViewHolder {
    Linkify.addLinks((get<View>(viewId) as TextView?)!!, Linkify.ALL)
    return this
}

/**
 * TextView 设置Typeface
 *
 * @param viewId   TextView 资源id
 * @param typeface [TextView.setTypeface]
 * @return ViewHolder
 */
fun RecyclerView.ViewHolder.setTypeface(
    @IdRes viewId: Int,
    typeface: Typeface?
): RecyclerView.ViewHolder {
    val text = get<TextView>(viewId)
    text.typeface = typeface
    text.paintFlags = text.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
    return this
}

/**
 * 获取EditText
 *
 * @param viewId EditText 资源id
 * @return EditText
 */
fun RecyclerView.ViewHolder.edit(@IdRes viewId: Int): EditText {
    return get(viewId)
}

/**
 * 获取ImageView
 *
 * @param viewId ImageView 资源id
 * @return ImageView
 */
fun RecyclerView.ViewHolder.image(@IdRes viewId: Int): ImageView {
    return get(viewId)
}

/**
 * 获取ProgressBar
 *
 * @param viewId ProgressBar 资源id
 * @return ProgressBar
 */
fun RecyclerView.ViewHolder.progress(@IdRes viewId: Int): ProgressBar {
    return get(viewId)
}

/**
 * ProgressBar 设置进度
 *
 * @param viewId   ProgressBar 资源id
 * @param progress progress
 * @param max      最大值
 * @return ViewHolder
 */
fun RecyclerView.ViewHolder.setProgress(
    @IdRes viewId: Int,
    progress: Int,
    max: Int
): RecyclerView.ViewHolder {
    val view = get<ProgressBar>(viewId)
    view.max = max
    view.progress = progress
    return this
}

/**
 * 获取RatingBar
 *
 * @param viewId RatingBar 资源id
 * @return RatingBar
 */
fun RecyclerView.ViewHolder.rating(@IdRes viewId: Int): RatingBar {
    return get(viewId)
}

/**
 * RatingBar 设置rating
 *
 * @param viewId RatingBar 资源id
 * @param rating rating
 * @param max    最大值
 * @return ViewHolder
 */
fun RecyclerView.ViewHolder.setRating(
    @IdRes viewId: Int,
    rating: Float,
    max: Int
): RecyclerView.ViewHolder {
    val view = get<RatingBar>(viewId)
    view.max = max
    view.rating = rating
    return this
}

/**
 * 获取CompoundButton
 *
 * @param viewId CompoundButton 资源id
 * @return CompoundButton
 */
fun RecyclerView.ViewHolder.compoundButton(@IdRes viewId: Int): CompoundButton {
    return get(viewId)
}

/**
 * View 设置Checked
 *
 * @param viewId  Checkable 资源id
 * @param checked checked
 * @return ViewHolder
 */
fun RecyclerView.ViewHolder.setChecked(
    @IdRes viewId: Int,
    checked: Boolean
): RecyclerView.ViewHolder {
    val view = get<View>(viewId)
    // View unable cast to Checkable
    if (view is Checkable) {
        (view as Checkable).isChecked = checked
    }
    return this
}

/**
 * @author coderyong
 */
abstract class BaseRecyclerAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var mList: MutableList<T> = ArrayList()
    private val mSpecialItems = SparseArray<View>()
    private var mListener: OnItemClickListener? = null

    /**
     * 创建ItemView
     *
     * @param parent   [RecyclerView.Adapter.onCreateViewHolder]
     * @param viewType [RecyclerView.Adapter.onCreateViewHolder]
     * @return View
     */
    protected abstract fun itemView(parent: ViewGroup, viewType: Int): View

    /**
     * 绑定条目
     *
     * @param holder   view持有者
     * @param position 条目位置
     */
    abstract fun onBindView(holder: RecyclerView.ViewHolder, position: Int)

    override fun getItemViewType(position: Int): Int {
        if (mSpecialItems.size() > 0) {
            if (mSpecialItems[position] != null) {
                return position
            }
            if (position == itemCount - 1 && mSpecialItems[Int.MAX_VALUE] != null) {
                return Int.MAX_VALUE
            }
        }
        if (this is IMultiple) {
            val itemType = (this as IMultiple).itemType(position)
            if (itemType < 0) {
                throw IndexOutOfBoundsException("ItemType must be greater than 0")
            }
            return -itemType
        }
        return super.getItemViewType(position)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (recyclerView.layoutManager == null) {
            recyclerView.layoutManager = GridLayoutManager(recyclerView.context, 1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
            mSpecialItems[viewType, itemView(
                parent,
                if (this is IMultiple) -viewType else viewType
            )]!!
        ) {}
    }

    /**
     * 根据资源ID创建ItemView
     *
     * @param parent           {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}
     * @param layoutResId      ItemView资源ID
     * @param isHorizontalFill 是否为水平填充
     * @return ItemView
     */
    fun createItemByLayoutId(
        @NonNull
        parent: ViewGroup,
        @LayoutRes
        layoutResId: Int,
        isHorizontalFill: Boolean = true,
    ): View {
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutResId, parent, false)
        var params = view.layoutParams
        if (params == null) {
            params =
                if (isHorizontalFill) ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                else ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        } else {
            params.width = if (isHorizontalFill) MATCH_PARENT else WRAP_CONTENT
        }
        view.layoutParams = params
        return view
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var item = position
        if (mSpecialItems.size() > 0) {
            //排除底部View
            if (item == itemCount - 1 && mSpecialItems[Int.MAX_VALUE] != null) {
                return
            }
            //排除对应位置特殊Item
            item -= if (mSpecialItems[item] != null) {
                return
            } else {
                var diff = 0
                for (i in 0 until item) {
                    val view = mSpecialItems[i]
                    if (view != null) {
                        diff++
                    }
                }
                diff
            }
        }
        onBindView(holder, item)
        if (mListener != null) {
            holder.itemView.setOnClickListener { mListener!!.onItemClick(item) }
        }
    }

    override fun getItemCount(): Int {
        return mList.size + mSpecialItems.size()
    }

    /**
     * 添加特殊ItemView
     *
     * @param position item位置
     * @param view     布局view
     */
    fun addView(position: Int, view: View) {
        mSpecialItems.put(position, view)
    }

    /**
     * 添加头View
     *
     * @param view 布局view
     */
    fun addHeaderView(view: View) {
        addView(0, view)
    }

    /**
     * 添加尾View
     *
     * @param view 布局view
     */
    fun addFooterView(view: View) {
        addView(Int.MAX_VALUE, view)
    }

    fun addData(vararg ts: T) {
        mList.addAll(listOf(*ts))
        notifyDataSetChanged()
    }

    fun addData(list: List<T>?) {
        if (list != null) {
            mList.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun replaceData(list: List<T>?) {
        mList.clear()
        if (list != null) {
            mList.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun getChildAt(position: Int): T {
        return mList[position]
    }

    val data: List<T>
        get() = mList

    fun clearData() {
        mList.clear()
        notifyDataSetChanged()
    }

    fun clear() {
        mList.clear()
        mSpecialItems.clear()
        notifyDataSetChanged()
    }

    fun compareNotify(newList: List<T>, callback: CompareCallBack<T>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return mList.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return callback.compareItem(
                    mList[oldItemPosition],
                    newList[newItemPosition]
                )
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return areItemsTheSame(oldItemPosition, newItemPosition)
                        && callback.compareContent(
                    mList[oldItemPosition],
                    newList[newItemPosition]
                )
            }
        }, true)
        replaceData(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    interface CompareCallBack<T> {
        /**
         * 比较两个对象
         *
         * @param oldObject 旧的对象
         * @param newObject 新的对象
         * @return true为相同
         */
        fun compareItem(oldObject: T, newObject: T): Boolean
        fun compareContent(oldObject: T, newObject: T): Boolean {
            return compareItem(oldObject, newObject)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    interface OnItemClickListener {
        /**
         * 去除特殊Item的点击事件
         *
         * @param position 点击的Item
         */
        fun onItemClick(position: Int)
    }

    interface IMultiple {
        /**
         * 根据数据位置创建Item类型
         *
         * @param position 数据位置
         * @return itemType
         */
        fun itemType(position: Int): Int
    }
}