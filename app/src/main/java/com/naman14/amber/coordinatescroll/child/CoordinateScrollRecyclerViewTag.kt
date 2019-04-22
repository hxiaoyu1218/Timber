package com.naman14.amber.coordinatescroll.child

import android.support.v7.widget.RecyclerView

/**
 * @author zhaoshe(zhaoshe@foxmail.com)
 * @since 2018/8/24
 */
internal class CoordinateScrollRecyclerViewTag(val recyclerView: RecyclerView)
    : RecyclerView.OnScrollListener(), CoordinateScrollChild {

    init {
        recyclerView.addOnScrollListener(this)
        recyclerView.isVerticalScrollBarEnabled = false
    }

    private var consumedY = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        consumedY = dy
    }

    override fun scrollBy(dy: Int): Int {
        consumedY = 0
        recyclerView.scrollBy(0, dy)
        return consumedY
    }

    override fun scrollToTop() {
        recyclerView.scrollBy(0, -Int.MAX_VALUE)
    }

    override fun scrollToBottom() {
        recyclerView.scrollBy(0, Int.MAX_VALUE)
    }
}