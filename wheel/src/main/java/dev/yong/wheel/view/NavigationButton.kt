@file:Suppress("unused")

package dev.yong.wheel.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import kotlin.math.abs

/**
 * @author coderyong
 */
@SuppressLint("AppCompatCustomView")
class NavigationButton : RadioButton {
    var message: String? = null

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.radioButtonStyle
    ) : super(context, attrs, defStyleAttr) {
        setButtonDrawable(0)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        setButtonDrawable(0)
    }

    /**
     * {@inheritDoc}
     *
     *
     * If the radio button is already checked, this method will not toggle the radio button.
     */
    override fun toggle() {
        // we override to prevent toggle when the radio is already
        // checked (as opposed to check boxes widgets)
        if (!isChecked) {
            super.toggle()
        }
    }

    override fun getAccessibilityClassName(): CharSequence {
        return NavigationButton::class.java.name
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (message != null) {
            val width = width
            val paint: Paint = paint
            //获取标注文本宽度
            val labelWidth = paint.measureText(message)
            //获取标注文本高度
            val fontMetrics = paint.fontMetrics
            val labelHeight = abs(fontMetrics.ascent) - fontMetrics.descent
            //文字的x轴左边坐标
            val x: Float
            //文字的y轴底边坐标
            val y: Float
            //设置标注背景颜色
            paint.color = Color.RED
            if (labelWidth > labelHeight) {
                val textPadding = labelHeight / 2
                val left = width - paddingRight - 2 * textPadding - labelWidth
                val top = paddingTop.toFloat()
                val right = (width - paddingRight).toFloat()
                val bottom = paddingTop + 2 * textPadding + labelHeight
                canvas.drawRoundRect(
                    RectF(left, top, right, bottom),
                    labelHeight,
                    labelHeight,
                    paint
                )
                x = left + textPadding
                y = bottom - textPadding
            } else {
                val radius = labelHeight / 2 + labelWidth / 2
                val cx = width - paddingRight - radius
                val cy = paddingTop + radius
                canvas.drawCircle(cx, cy, radius, paint)
                x = cx - labelWidth / 2 + 1
                y = cy + labelHeight / 2
            }
            paint.color = Color.WHITE
            canvas.drawText(message!!, x, y, paint)
        }
    }
}