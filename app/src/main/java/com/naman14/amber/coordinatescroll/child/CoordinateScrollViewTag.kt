package com.naman14.amber.coordinatescroll.child


import android.view.ViewGroup

/**
 * @author zhaoshe(zhaoshe@foxmail.com)
 * @since 2018/8/24
 */
internal class CoordinateScrollViewTag<T : ViewGroup>(val view: T): CoordinateScrollChild {

    init {
        view.isVerticalScrollBarEnabled = false
    }

    override fun scrollBy(dy: Int): Int {
        val oldScroll = view.scrollY
        view.scrollBy(0, dy)
        return view.scrollY - oldScroll
    }

    override fun scrollToTop() {
        view.scrollBy(0, -view.scrollY)
    }

    override fun scrollToBottom() {
        view.scrollBy(0, getMaxChildBottom() - view.scrollY)
    }

    private fun getMaxChildBottom(): Int {
        var maxBottom = 0
        for (i in 0 until view.childCount) {
            val child = view.getChildAt(i) ?: continue
            val lp = child.layoutParams as? ViewGroup.MarginLayoutParams
            maxBottom = Math.max(maxBottom, child.bottom + (lp?.bottomMargin ?: 0))
        }

        return maxBottom
    }
}