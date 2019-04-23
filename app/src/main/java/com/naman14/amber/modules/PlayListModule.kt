package com.naman14.amber.modules

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.naman14.amber.R
import com.naman14.amber.adapters.PlayListAdapter
import com.naman14.amber.fragments.OnlineMainFragment
import com.naman14.amber.services.PlayListModel
import com.naman14.amber.utils.UIUtils

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/23
 **/
class PlayListModule(val f: OnlineMainFragment, val data: PlayListModel) {
    val view = PlayListView(f.context)
            .setCallBackFragment(f)
            .init()
            .bindData(data)

    class PlayListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
        : RelativeLayout(context, attrs, defStyleAttr) {

        lateinit var title: TextView
        lateinit var recyclerView: RecyclerView
        lateinit var adapter: PlayListAdapter
        lateinit var fragment: OnlineMainFragment

        init {
            initView()
        }

        private fun initView() {
            View.inflate(context, R.layout.daily_hot_song_vh, this)
            layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            recyclerView = findViewById(R.id.online_song_list)
            val margin = UIUtils.dip2Px(context, 8).toInt()
            UIUtils.updateLayoutMargin(recyclerView, margin, margin, margin, 0)
            title = findViewById(R.id.text)
            recyclerView.layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        }

        fun setCallBackFragment(f: OnlineMainFragment): PlayListView {
            fragment = f
            return this
        }

        fun init(): PlayListView {
            adapter = PlayListAdapter(fragment)
            recyclerView.adapter = adapter
            return this
        }

        fun bindData(cell: PlayListModel): PlayListView {
            if (cell.playList.isNullOrEmpty()) {
                return this
            }
            adapter.bindData(cell.playList)
            return this
        }
    }
}