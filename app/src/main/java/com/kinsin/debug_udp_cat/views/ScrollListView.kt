package com.kinsin.debug_udp_cat.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ListView
import android.widget.ScrollView


class ScrollListView : ListView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) :super(context, attributeSet, defStyle)

    private var father: ScrollView? = null

    /**
     * 如果需要ListView可以滑动，则需要设置这个方法，将scrollView传入
     */
    fun setFatherToEnableTouch(scrollView: ScrollView) {
        this.father = scrollView
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpace = MeasureSpec.makeMeasureSpec(600, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, expandSpace)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (father == null) {
            return super.dispatchTouchEvent(ev)
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> father!!.requestDisallowInterceptTouchEvent(
                true
            )
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> father!!.requestDisallowInterceptTouchEvent(
                false
            )
        }
        return super.dispatchTouchEvent(ev)
    }

}