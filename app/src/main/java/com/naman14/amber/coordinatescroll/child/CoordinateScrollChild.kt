package com.naman14.amber.coordinatescroll.child

/**
 * @author zhaoshe(zhaoshe@foxmail.com)
 * @since 2018/8/23
 */
interface CoordinateScrollChild {
    /**
     * 滚动 dy，返回值是实际滚动的距离
     */
    fun scrollBy(dy: Int): Int

    /**
     * 滚动到顶部
     */
    fun scrollToTop()

    /**
     * 滚动到底部
     */
    fun scrollToBottom()
}