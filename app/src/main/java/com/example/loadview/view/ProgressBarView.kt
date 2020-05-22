package com.example.loadview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.widget.ProgressBar
import com.example.loadview.R

/**
 * create by zhangshi on 2020/5/22.
 */
open class ProgressBarView : ProgressBar {

    lateinit var mContext: Context
    protected var mTextSize = 12  //字体大小
    protected var mTextColor = Color.BLACK //字体颜色
    protected var mUnReachColor = Color.GREEN //没有到达(右边progressbar的颜色)
    protected var mProgressHeight = 6 //progressbar的高度
    protected var mReachColor = mTextColor //progressbar进度的颜色
    protected var mTextOffset = 10 //字体间距
    protected lateinit var mPaint: Paint
    protected var mRealWith = 0 //progressbar真正的宽度

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAtts: Int) : super(context, attrs, defStyleAtts) {
        init(context, attrs)
    }

    private fun init(context: Context, attributeSet: AttributeSet?) {
        mContext = context
        obtainStyleAttrs(attributeSet)
        mPaint = Paint()
        mPaint.textSize = mTextSize.toFloat()
    }

    /**
     * 获取属性
     */
    private fun obtainStyleAttrs(attrs: AttributeSet?) {
        val ta = mContext.obtainStyledAttributes(attrs, R.styleable.ProgressBarView)
        mTextSize = ta.getDimensionPixelSize(
            R.styleable.ProgressBarView_progress_text_size,
            sp2px(mTextSize)
        )
        mTextColor = ta.getColor(R.styleable.ProgressBarView_progress_text_color, mTextColor)

        mUnReachColor =
            ta.getColor(R.styleable.ProgressBarView_progress_unreach_color, mUnReachColor)
        mProgressHeight =
            ta.getDimension(R.styleable.ProgressBarView_progress_height, dp2px(mProgressHeight))
                .toInt()

        mReachColor = ta.getColor(R.styleable.ProgressBarView_progress_reach_color, mReachColor)
        mTextOffset =
            ta.getDimension(R.styleable.ProgressBarView_progress_text_offset, dp2px(mTextOffset))
                .toInt()

        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthAval = MeasureSpec.getSize(widthMeasureSpec)
        val height = mesureHeight(heightMeasureSpec)
        setMeasuredDimension(widthAval, height)
        //计算progressbar真正宽度=控件的宽度-paddingleft-paddingright
        mRealWith = measuredWidth - paddingLeft - paddingRight
    }

    fun mesureHeight(heightMeasureSpec: Int): Int {
        var result = 0
        //获取高度模式
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        //获取高度
        val size = MeasureSpec.getSize(heightMeasureSpec)
        if (mode == MeasureSpec.EXACTLY) {
            //精准模式 用户设置为 比如80dp  match_parent fill_parent
            result = size
        } else {
            //计算中间文字的高度
            val textHeight = mPaint.descent() - mPaint.ascent()
            //paddingTop+paddingBottom+ progressbar高度和文字高度的最大值
            result = paddingTop + paddingBottom + Math.max(mProgressHeight, Math.abs(textHeight).toInt())
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size)
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {

        Log.e("xxxxx", "onDraw")
        //保存画布
        canvas.save()
        //移动画布
        canvas.translate(paddingLeft.toFloat(), (height / 2).toFloat())
        //定义变量用来控制是否要绘制右边progressbar  如果宽度不够的时候就不进行绘制
        var noNeedUnRech = false
        //计算左边进度在整个空间宽度的占比
        val radio = progress.toFloat() / max
        //获取左边进度的宽度
        var progressX = radio * mRealWith
        //中间文字
        val text = progress.toString() + "%"
        //获取文字的宽度
        val textWidth = mPaint.measureText(text)
        if (progressX + textWidth > mRealWith) {
            //左边进度+文字的宽度超过progressbar的宽度 重新计算左边进度的宽度 这个时候也就意味着不需要绘制右边进度
            progressX = mRealWith - textWidth
            noNeedUnRech = true
        }
        //计算左边进度结束的位置 如果结束的位置小于0就不需要绘制左边的进度
        val endX = progressX - mTextOffset / 2
        if (endX > 0) {
            //绘制左边进度
            mPaint.setColor(mReachColor)
            mPaint.strokeWidth = mProgressHeight.toFloat()
            canvas.drawLine(0f, 0f, endX, 0f, mPaint)
        }
        mPaint.setColor(mTextColor)
        if (progress != 0) {
            //计算文字基线
            val y = (-(mPaint.descent() + mPaint.ascent()) / 2)
            //绘制文字
            canvas.drawText(text, progressX, y, mPaint)
        }
        if (!noNeedUnRech) {
            //右边进度的开始位置=左边进度+文字间距的一半+文字宽度
            var start = 0f;
            if (progress == 0) {
                start = progressX
            } else {
                start = progressX + mTextOffset / 2 + textWidth
            }
            mPaint.setColor(mUnReachColor)
            mPaint.strokeWidth = mProgressHeight.toFloat()
            //绘制右边进度
            canvas.drawLine(start, 0f, mRealWith.toFloat(), 0f, mPaint)
        }
        //重置画布
        canvas.restore()
    }


    protected fun dp2px(dip: Int): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip.toFloat(),
            resources.displayMetrics
        )

    protected fun sp2px(sp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp.toFloat(),
            getResources().getDisplayMetrics()
        ).toInt()

}