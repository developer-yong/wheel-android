package dev.yong.sample.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.yong.sample.R
import dev.yong.wheel.base.adapter.BaseRecyclerAdapter
import dev.yong.wheel.base.adapter.image
import dev.yong.wheel.base.adapter.text
import dev.yong.wheel.utils.Logger

/**
 * @author coderyong
 */
class ListAdapter : BaseRecyclerAdapter<String>() {

    private val mImageResource = intArrayOf(
        R.mipmap.bg_curriculum_lesson1,
        R.mipmap.bg_curriculum_lesson2,
        R.mipmap.bg_curriculum_lesson3,
        R.mipmap.bg_curriculum_lesson4,
        R.mipmap.bg_curriculum_lesson5,
        R.mipmap.bg_curriculum_lesson6,
        R.mipmap.bg_curriculum_lesson7,
        R.mipmap.bg_curriculum_lesson8
    )

    override fun itemView(parent: ViewGroup, viewType: Int): View {
        return createItemByLayoutId(parent, R.layout.item_list)
//        return createItemByLayoutId(parent,
//            if (viewType == 1) android.R.layout.simple_list_item_1 else android.R.layout.simple_list_item_2)
    }

    override fun onBindView(holder: RecyclerView.ViewHolder, position: Int) {
        holder.image(R.id.iv_image)
            .setImageResource(mImageResource[position % 8])

        holder.text(R.id.tv_text).text = getChildAt(position)
    }

//    override fun itemType(position: Int): Int {
//        return if (position % 2 == 0) 1 else 2
//    }


}