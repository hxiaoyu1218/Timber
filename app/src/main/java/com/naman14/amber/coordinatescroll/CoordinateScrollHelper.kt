package com.naman14.amber.coordinatescroll

import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.naman14.amber.R
import com.naman14.amber.coordinatescroll.child.CoordinateScrollChild
import com.naman14.amber.coordinatescroll.child.CoordinateScrollRecyclerViewTag
import com.naman14.amber.coordinatescroll.child.CoordinateScrollViewTag
internal class CoordinateScrollHelper(val layout: CoordinateScrollLinearLayout) {

    fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        // scroll parent until reaching the target's edge
        if (dy != 0) {
            if (dy > 0) { // scroll down
                val bottom = getScrollingTop(target, layout) + target.height
                val delta = bottom - (layout.height - layout.paddingBottom)
                if (delta > 0) { // off screen
                    val consumeY = Math.min(dy, delta)
                    consumed[1] += consumeY
                    layout.scrollBy(0, consumeY)
                }
            } else {
                val top = getScrollingTop(target, layout)
                val delta = top - layout.paddingTop
                if (delta < 0) { // off screen
                    val consumeY = Math.max(dy, delta)
                    consumed[1] += consumeY
                    layout.scrollBy(0, consumeY)
                }
            }
        }
    }

    private fun getScrollingTop(view: View, viewGroup: ViewGroup): Int {
        var child = view
        var parent = view.parent
        var top = 0

        while (parent is ViewGroup && child != viewGroup) {
            top += child.top - parent.scrollY
            child = parent
            parent = child.parent
        }

        return top
    }

    fun dispatchNestedPreScrollToChild(dx: Int, dy: Int, consumed: IntArray): Boolean {
        if (dy == 0) {
            consumed[0] = 0
            consumed[1] = 0
            return false
        }

        var unConsumedY = dy
        var keptDy = 0
        if (dy > 0) {
            for (i in 0 until layout.childCount) {
                val child = layout.getChildAt(i) ?: continue
                val childBottom = getScrollingTop(child, layout) + child.height
                val delta = childBottom - (layout.height - layout.paddingBottom)

                if (delta >= 0) {
                    keptDy += delta
                    unConsumedY -= delta

                    if (unConsumedY <= 0) {
                        break
                    } else {
                        val child = getMatchParentCoordinateScrollChild(child)
                        if (child != null) {
                            unConsumedY -= child.scrollBy(unConsumedY)
                            if (unConsumedY <= 0) {
                                break
                            }
                        }
                    }
                }
            }
        } else {
            for (i in layout.childCount - 1 downTo 0) {
                val child = layout.getChildAt(i) ?: continue
                val childTop = getScrollingTop(child, layout)
                val delta = childTop - keptDy - layout.paddingTop
                if (delta <= 0) {
                    keptDy += delta
                    unConsumedY -= delta

                    if (unConsumedY >= 0) {
                        break
                    } else {
                        val child = getMatchParentCoordinateScrollChild(child)
                        if (child != null) {
                            unConsumedY -= child.scrollBy(unConsumedY)
                            if (unConsumedY >= 0) {
                                break
                            }
                        }
                    }
                }
            }
        }

        consumed[0] = 0
        consumed[1] = dy - unConsumedY - keptDy

        return true
    }

    private fun getMatchParentCoordinateScrollChild(view: View): CoordinateScrollChild? {
        if (view is CoordinateScrollChild) {
            return view
        }

        if (view is RecyclerView) {
            var tag = view.getTag(R.id.coordinate_scroll_tag) as? CoordinateScrollRecyclerViewTag
            if (tag == null) {
                tag = CoordinateScrollRecyclerViewTag(view)
                view.setTag(R.id.coordinate_scroll_tag, tag)
            }
            return tag
        }

        if (view is ScrollView || view is NestedScrollView) {
            var tag = view.getTag(R.id.coordinate_scroll_tag) as? CoordinateScrollChild
            if (tag == null) {
                tag = CoordinateScrollViewTag(view as ViewGroup)
                view.setTag(R.id.coordinate_scroll_tag, tag)
            }
            return tag
        }

        val tag = view.getTag(R.id.coordinate_scroll_tag) as? CoordinateScrollChild
        if (tag != null) {
            return tag
        }

        if (view !is ViewGroup) {
            return null
        }

        if (view.paddingTop > 0 || view.paddingBottom > 0) {
            return null
        }

        val ordered = ViewGroupUtility.getOrderedChildren(view)
        ordered.forEach {
            if (it.top == 0 && it.bottom == view.height) {
                val child = getMatchParentCoordinateScrollChild(it)
                if (child != null) {
                    return child
                }
            }
        }

        return null
    }

    fun getChildrenMeasureHeight(): Int {
        if (layout.childCount == 0) {
            return 0
        }

        var childrenHeight = 0
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i) ?: continue
            val lp = child.layoutParams as ViewGroup.MarginLayoutParams
            childrenHeight += child.measuredHeight + lp.topMargin + lp.bottomMargin
        }

        return childrenHeight
    }

    fun getChildrenHeight(): Int {
        if (layout.childCount == 0) {
            return 0
        }

        var childrenHeight = 0
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i) ?: continue
            val lp = child.layoutParams as ViewGroup.MarginLayoutParams
            childrenHeight += child.height + lp.topMargin + lp.bottomMargin
        }

        return childrenHeight
    }

    fun calculateFirstVisibleChildIndexAndOffset(): IntArray {
        if (layout.childCount == 0) {
            return intArrayOf(0, 0)
        }
        var scrollY = layout.scrollY
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i) ?: continue
            val lp = child.layoutParams as ViewGroup.MarginLayoutParams
            if (scrollY <= lp.topMargin) {
                return intArrayOf(i, -scrollY)
            }
            scrollY -= lp.topMargin
            if (scrollY <= child.height + lp.bottomMargin) {
                return intArrayOf(i, scrollY)
            }
            scrollY -= child.height + lp.bottomMargin
        }

        return intArrayOf(layout.childCount - 1, scrollY)
    }

    fun scrollToChildAt(index: Int, offset: Int) {
        if (layout.childCount == 0 || index < 0 || index >= layout.childCount) {
            return
        }

        var scrollY = offset
        for (i in 0 until index) {
            val child = layout.getChildAt(i) ?: continue
            val lp = child.layoutParams as ViewGroup.MarginLayoutParams
            scrollY += child.height + lp.topMargin + lp.bottomMargin
        }

        layout.scrollTo(layout.scrollX, scrollY)
    }

    fun scrollToChildAtAndResetNestedScroll(index: Int) {
        if (layout.childCount == 0 || index < 0 || index >= layout.childCount) {
            return
        }

        var scrollY = 0
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i) ?: continue
            val scrollChild = getMatchParentCoordinateScrollChild(child)
            if (i < index) {
                val lp = child.layoutParams as ViewGroup.MarginLayoutParams
                scrollY += child.height + lp.topMargin + lp.bottomMargin

                scrollChild?.scrollToBottom()
            } else {
                scrollChild?.scrollToTop()
            }
        }

        layout.scrollTo(layout.scrollX, scrollY)
    }

    fun getChildTopInCoordinateScroll(index: Int): Int {
        if (layout.childCount == 0 || index < 0 || index >= layout.childCount) {
            return 0
        }

        var scroll = 0
        for (i in 0 until index) {
            val child = layout.getChildAt(i) ?: continue
            val lp = child.layoutParams as ViewGroup.MarginLayoutParams
            scroll += child.height + lp.topMargin + lp.bottomMargin
        }

        val child = layout.getChildAt(index)
        val lp = child.layoutParams as ViewGroup.MarginLayoutParams
        scroll += lp.topMargin

        return scroll
    }

    fun getChildTopInCoordinateScroll(child: View): Int {
        if (layout.childCount == 0 || child.parent != layout) {
            return 0
        }

        var scroll = 0
        for (i in 0 until layout.childCount) {
            val temp = layout.getChildAt(i) ?: continue
            val lp = temp.layoutParams as ViewGroup.MarginLayoutParams
            if (temp === child) {
                scroll += lp.topMargin
                break
            } else {
                scroll += temp.height + lp.topMargin + lp.bottomMargin
            }
        }

        return scroll
    }

    fun resetChildrenScroll(targetScroll: Int) {
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i) ?: continue
            val childTop = child.top
            val scrollChild = getMatchParentCoordinateScrollChild(child)
            if (scrollChild != null) {
                if (childTop >= targetScroll) {
                    scrollChild.scrollToTop()
                } else {
                    scrollChild.scrollToBottom()
                }
            }
        }
    }
}