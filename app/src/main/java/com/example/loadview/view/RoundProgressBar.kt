package com.example.loadview.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.loadview.R
import kotlin.math.exp

/**
 * create by zhangshi on 2020/5/22.
 */
class RoundProgressBar : ProgressBarView {
    //半径
    private var mRadius = 30
    private var mMaxPaintWidth = 0


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAtts: Int) : super(
        context,
        attributeSet,
        defStyleAtts
    ) {
        //绘制圆形进度条的宽度   这里设置为长方形进度条高度的1.5倍
        mRealWith = (mProgressHeight * 1.5f).toInt()
        val typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.RoundProgressBar)
        mRadius = typedArray.getDimension(R.styleable.RoundProgressBar_radius, dp2px(mRadius)).toInt()
        mPaint.style = Paint.Style.STROKE
        mPaint.isAntiAlias = true
        mPaint.isDither =true
        mPaint.strokeCap = Paint.Cap.ROUND

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mMaxPaintWidth = mProgressHeight
        //计算控件的精准值
        var expect = mRadius * 2 + mMaxPaintWidth + paddingLeft + paddingRight
        var width = resolveSize(expect, widthMeasureSpec)
        var height = resolveSize(expect, heightMeasureSpec)
        var realWidth = Math.min(width, height)
        mRadius = (realWidth - paddingRight - paddingLeft - mMaxPaintWidth) / 2
        setMeasuredDimension(realWidth, realWidth)

    }

    override fun onDraw(canvas: Canvas) {
        //获取当前进度
        val text = progress.toString() + "%"
        //测量文字的宽度
        val textWidth = mPaint.measureText(text)
        //文字的高度
        val textHeight = (mPaint.descent() + mPaint.ascent()) / 2
        //保存画布
        canvas.save()
        //平移画布位置
        canvas.translate((paddingLeft + mMaxPaintWidth / 2).toFloat(),
            (paddingTop + mMaxPaintWidth / 2).toFloat()
        )
        mPaint.style = Paint.Style.STROKE
        mPaint.color = mUnReachColor
        mPaint.strokeWidth = mRealWith.toFloat()
        //绘制圆 要注意绘制圆的x y 因为上面对画布进行了平移所以这里就不需要计算了，如果画布没有进行平移需要计算 x y
        canvas.drawCircle(mRadius.toFloat(), mRadius.toFloat(), mRadius.toFloat(), mPaint)
        //绘制圆弧
        mPaint.color = mReachColor

        //计算圆弧的扫过的幅度
        val sweepAngle = progress * 1f / max * 360f
        val rectF: RectF = RectF(0f, 0f, (mRadius * 2).toFloat(), (mRadius * 2).toFloat())
        canvas.drawArc(rectF, 0f, sweepAngle, false, mPaint)
        //绘制中间文字
        mPaint.setColor(mTextColor)
        mPaint.style = Paint.Style.FILL
        canvas.drawText(text, mRadius - textWidth / 2, mRadius - textHeight, mPaint)
        canvas.restore()
    }

}