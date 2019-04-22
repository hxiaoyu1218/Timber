package com.naman14.amber.coordinatescroll

import android.content.Context
import android.os.Build
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import java.util.*

/**
 * Inspired by FrameLayout and LinearLayout
 */
open class SectionLinearLayout @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val MAX_HEIGHT_SIZE = 1073741823
    }

    private var mGravity = Gravity.START or Gravity.TOP

    private var mMeasureAllChildren = false
    private val mMatchParentChildren = ArrayList<View>(1)

    private var mTotalLength = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var count = childCount

        val measureMatchParentChildren =
                View.MeasureSpec.getMode(widthMeasureSpec) != View.MeasureSpec.EXACTLY
                        || View.MeasureSpec.getMode(heightMeasureSpec) != View.MeasureSpec.EXACTLY
        mMatchParentChildren.clear()

        val widthSpec = widthMeasureSpec
        val heightSpec = heightMeasureSpec

        var totalHeight = 0
        var maxWidth = 0
        var childState = 0

        for (i in 0 until count) {
            val child = getChildAt(i)
            if (mMeasureAllChildren || child.visibility != View.GONE) {
                val lp = child.layoutParams as LayoutParams
                measureChildWithMargins(child, widthSpec, 0,
                        checkHeightMeasureSpec(heightSpec, lp), 0)
                maxWidth = Math.max(maxWidth,
                        child.measuredWidth + lp.leftMargin + lp.rightMargin)
                totalHeight += child.measuredHeight + lp.topMargin + lp.bottomMargin
                childState = View.combineMeasuredStates(childState, child.measuredState)
                if (measureMatchParentChildren) {
                    if (lp.width == LayoutParams.MATCH_PARENT || lp.height == LayoutParams.MATCH_PARENT) {
                        mMatchParentChildren.add(child)
                    }
                }
            }
        }

        // Account for padding too
        maxWidth += getPaddingLeftWithForeground() + getPaddingRightWithForeground()
        totalHeight += getPaddingTopWithForeground() + getPaddingBottomWithForeground()

        // Check against our minimum height and width
        totalHeight = Math.max(totalHeight, suggestedMinimumHeight)
        maxWidth = Math.max(maxWidth, suggestedMinimumWidth)

        // Check against our foreground's minimum height and width
        val drawable = foreground
        if (drawable != null) {
            totalHeight = Math.max(totalHeight, drawable.minimumHeight)
            maxWidth = Math.max(maxWidth, drawable.minimumWidth)
        }

        setMeasuredDimension(View.resolveSizeAndState(maxWidth, widthSpec, childState),
                View.resolveSizeAndState(totalHeight, heightSpec,
                        childState shl View.MEASURED_HEIGHT_STATE_SHIFT))

        val widthMeasureSize = MeasureSpec.getSize(widthSpec)
        val heightMeasureSize = MeasureSpec.getSize(heightSpec)

        var correctedWidth = measuredWidth
        var correctedHeight = measuredHeight

        count = mMatchParentChildren.size
        if (count > 1) {
            for (i in 0 until count) {
                val child = mMatchParentChildren[i]
                val lp = child.layoutParams as ViewGroup.MarginLayoutParams

                val childWidthMeasureSpec: Int
                childWidthMeasureSpec = if (lp.width == LayoutParams.MATCH_PARENT) {
                    maxWidth = Math.max(maxWidth, widthMeasureSize)
                    val matchWidth = Math.max(0, widthMeasureSize
                            - getPaddingLeftWithForeground() - getPaddingRightWithForeground()
                            - lp.leftMargin - lp.rightMargin)
                    correctedWidth = Math.max(correctedWidth, matchWidth)
                    View.MeasureSpec.makeMeasureSpec(
                            matchWidth, View.MeasureSpec.EXACTLY)
                } else {
                    ViewGroup.getChildMeasureSpec(widthSpec,
                            getPaddingLeftWithForeground() + getPaddingRightWithForeground() +
                                    lp.leftMargin + lp.rightMargin,
                            lp.width)
                }

                val childHeightMeasureSpec: Int
                childHeightMeasureSpec = if (lp.height == LayoutParams.MATCH_PARENT) {
                    totalHeight += heightMeasureSize - lp.topMargin - lp.bottomMargin - child.measuredHeight
                    val matchHeight = Math.max(0, heightMeasureSize
                            - getPaddingTopWithForeground() - getPaddingBottomWithForeground()
                            - lp.topMargin - lp.bottomMargin)
                    correctedHeight = Math.max(correctedHeight, matchHeight)
                    View.MeasureSpec.makeMeasureSpec(
                            matchHeight, View.MeasureSpec.EXACTLY)
                } else {
                    ViewGroup.getChildMeasureSpec(heightSpec,
                            (getPaddingTopWithForeground() + getPaddingBottomWithForeground() +
                                    lp.topMargin + lp.bottomMargin),
                            lp.height)
                }

                child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            }
        }

        if (correctedWidth != measuredWidth || correctedHeight != measuredHeight) {
            setMeasuredDimension(View.resolveSizeAndState(maxWidth, widthSpec, childState),
                    View.resolveSizeAndState(totalHeight, heightSpec,
                            childState shl View.MEASURED_HEIGHT_STATE_SHIFT))
        }

        mTotalLength = totalHeight - getPaddingTopWithForeground() - getPaddingBottomWithForeground()
    }

    private fun checkHeightMeasureSpec(spec: Int, childLp: LayoutParams): Int {
        return if (ViewGroup.LayoutParams.WRAP_CONTENT == childLp.height) {
            MeasureSpec.makeMeasureSpec(MAX_HEIGHT_SIZE, MeasureSpec.getMode(spec))
        } else {
            spec
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var childTop: Int
        var childLeft: Int

        // Where right end of child should go
        val width = right - left
        val childRight = width - paddingRight

        // Space available for child
        val childSpace = width - paddingLeft - paddingRight

        val count = childCount

        val majorGravity = mGravity and Gravity.VERTICAL_GRAVITY_MASK
        val minorGravity = mGravity and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK

        childTop = when (majorGravity) {
            Gravity.BOTTOM ->
                // mTotalLength contains the padding already
                paddingTop + bottom - top - mTotalLength
        // mTotalLength contains the padding already
            Gravity.CENTER_VERTICAL -> paddingTop + (bottom - top - mTotalLength) / 2

            Gravity.TOP -> paddingTop
            else -> paddingTop
        }

        var i = 0
        while (i < count) {
            val child = getChildAt(i)
            if (child != null && child.visibility != View.GONE) {
                val childWidth = child.measuredWidth
                val childHeight = child.measuredHeight

                val lp = child.layoutParams as LayoutParams

                var gravity = lp.gravity
                if (gravity < 0) {
                    gravity = minorGravity
                }
                val layoutDirection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutDirection
                } else {
                    ViewCompat.LAYOUT_DIRECTION_LTR
                }
                val absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection)
                childLeft = when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                    Gravity.CENTER_HORIZONTAL -> (paddingLeft + (childSpace - childWidth) / 2
                            + lp.leftMargin) - lp.rightMargin

                    Gravity.RIGHT -> childRight - childWidth - lp.rightMargin

                    Gravity.LEFT -> paddingLeft + lp.leftMargin
                    else -> paddingLeft + lp.leftMargin
                }

                childTop += lp.topMargin
                setChildFrame(child, childLeft, childTop + 0,
                        childWidth, childHeight)
                childTop += childHeight + lp.bottomMargin + 0
            }
            i++
        }
    }

    private fun getPaddingLeftWithForeground(): Int {
//        return if (isForegroundInsidePadding())
//            Math.max(mPaddingLeft, mForegroundPaddingLeft)
//        else
//            mPaddingLeft + mForegroundPaddingLeft
        return paddingLeft
    }

    private fun getPaddingRightWithForeground(): Int {
//        return if (isForegroundInsidePadding())
//            Math.max(mPaddingRight, mForegroundPaddingRight)
//        else
//            mPaddingRight + mForegroundPaddingRight
        return paddingRight
    }

    private fun getPaddingTopWithForeground(): Int {
//        return if (isForegroundInsidePadding())
//            Math.max(mPaddingTop, mForegroundPaddingTop)
//        else
//            mPaddingTop + mForegroundPaddingTop
        return paddingTop
    }

    private fun getPaddingBottomWithForeground(): Int {
//        return if (isForegroundInsidePadding())
//            Math.max(mPaddingBottom, mForegroundPaddingBottom)
//        else
//            mPaddingBottom + mForegroundPaddingBottom
        return paddingBottom
    }

    private fun setChildFrame(child: View, left: Int, top: Int, width: Int, height: Int) {
        child.layout(left, top, left + width, top + height)
    }

}