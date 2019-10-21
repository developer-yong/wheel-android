package dev.yong.wheel.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RadioButton;

import androidx.annotation.RequiresApi;

/**
 * @author coderyong
 */
@SuppressLint("AppCompatCustomView")
public class NavigationButton extends RadioButton {

    private String mMessage;

    public NavigationButton(Context context) {
        this(context, null);
    }

    public NavigationButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.radioButtonStyle);
    }

    public NavigationButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setButtonDrawable(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NavigationButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setButtonDrawable(0);
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the radio button is already checked, this method will not toggle the radio button.
     */
    @Override
    public void toggle() {
        // we override to prevent toggle when the radio is already
        // checked (as opposed to check boxes widgets)
        if (!isChecked()) {
            super.toggle();
        }
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return NavigationButton.class.getName();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mMessage != null) {
            int width = getWidth();
            Paint paint = getPaint();
            //获取标注文本宽度
            float labelWidth = paint.measureText(mMessage);
            //获取标注文本高度
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float labelHeight = Math.abs(fontMetrics.ascent) - fontMetrics.descent;
            //文字的x轴左边坐标
            float x;
            //文字的y轴底边坐标
            float y;
            //设置标注背景颜色
            paint.setColor(Color.RED);
            if (labelWidth > labelHeight) {
                float textPadding = labelHeight / 2;

                float left = width - getPaddingRight() - 2 * textPadding - labelWidth;
                float top = getPaddingTop();
                float right = width - getPaddingRight();
                float bottom = getPaddingTop() + 2 * textPadding + labelHeight;

                canvas.drawRoundRect(new RectF(left, top, right, bottom), labelHeight, labelHeight, paint);

                x = left + textPadding;
                y = bottom - textPadding;
            } else {
                float radius = labelHeight / 2 + labelWidth / 2;
                float cx = width - getPaddingRight() - radius;
                float cy = getPaddingTop() + radius;

                canvas.drawCircle(cx, cy, radius, paint);

                x = cx - labelWidth / 2 + 1;
                y = cy + labelHeight / 2;
            }
            paint.setColor(Color.WHITE);
            canvas.drawText(mMessage, x, y, paint);
        }
    }
}
