package com.naman14.timber.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.naman14.timber.R
import com.naman14.timber.utils.UIUtils

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/7
 **/
class SingleTabLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val DARK = 0
        const val LIGHT = 1
    }

    private var textList = ArrayList<String>()
    private var currentTheme = DARK
    private var textDrawableList = ArrayList<CustomTextDrawable>()
    private var offset = 0f
    private var position = 0
    private var realPos = 0
    private var isLeft = false

    private var viewPager: ViewPager? = null

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        var x = paddingLeft.toFloat()
        for (i in textDrawableList.indices) {
            canvas?.translate(x, 0f)
            val drawable = textDrawableList[i]
            if (i == position) {
                drawable.alpha = getDrawAlpha()
            } else if (i == realPos) {
                drawable.alpha = getRDrawAlpha()
            } else {
                drawable.alpha = 127
            }
            val textWidth = drawable.intrinsicWidth
            val textHeight = drawable.intrinsicHeight
            canvas?.save()
            canvas?.translate(0f,
                    ((height - textHeight) / 2).toFloat())
            drawable.draw(canvas)
            canvas?.restore()
            x = textWidth + UIUtils.dip2Px(context, 32)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val x = it.x
            var t = paddingLeft
            for (i in textDrawableList.indices) {
                val drawable = textDrawableList[i]
                t += drawable.intrinsicWidth
                if (x <= t) {
                    viewPager?.currentItem = i
                    return true
                } else {
                    t += UIUtils.dip2Px(context, 32).toInt()
                }
            }
        }
        super.onTouchEvent(event)
        return false
    }


    fun bindViewPager(viewPager: ViewPager) {
        this.viewPager = viewPager
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position != realPos) {
                    //offset 1-0
                    this@SingleTabLayout.position = position
                    isLeft = false
                } else {
                    //offset 0-1
                    this@SingleTabLayout.position = position + 1
                    isLeft = true
                }
                offset = positionOffset
                invalidate()
            }

            override fun onPageScrollStateChanged(p0: Int) {
                if (p0 == 1) {
                    realPos = viewPager.currentItem
                }
            }

            override fun onPageSelected(p0: Int) {
            }
        })
    }

    fun setTheme(isDark: Boolean) {
        if (isDark) {
            currentTheme = DARK
            setBackgroundColor(resources.getColor(R.color.window_background_dark))
        } else {
            currentTheme = LIGHT
            setBackgroundColor(resources.getColor(R.color.window_background))
        }
        for (drawable in textDrawableList) {
            drawable.setTextColor(if (currentTheme == DARK) Color.WHITE else Color.BLACK)
        }
    }

    fun initTabs(list: List<String>) {
        textList.clear()
        textDrawableList.clear()
        textList.addAll(list)
        //textList.addAll(listOf(resources.getString(R.string.songs), resources.getString(R.string.albums), resources.getString(R.string.artists)))
        for (text in textList) {
            val textDrawable = CustomTextDrawable(context)
            textDrawable.textSize = 20f
            textDrawable.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            textDrawable.text = text
            textDrawableList.add(textDrawable)
        }
    }

    private fun getDrawAlpha(): Int {
        if (isLeft) {
            return (127 + 128 * offset).toInt()
        } else {
            return (127 + 128 * (1 - offset)).toInt()
        }
    }

    private fun getRDrawAlpha(): Int {
        if (isLeft) {
            return (255 - 128 * offset).toInt()
        } else {
            return (255 - 128 * (1 - offset)).toInt()
        }
    }
}