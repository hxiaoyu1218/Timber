package com.naman14.amber.modules

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.naman14.amber.R
import com.naman14.amber.adapters.OnlineSongListAdapter
import com.naman14.amber.fragments.OnlineMainFragment
import com.naman14.amber.services.LatestSongModel

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/23
 **/

class LatestSongModule(val f: OnlineMainFragment, val data: LatestSongModel) {

    val view = LatestSongView(f.context)
            .setCallBackFragment(f)
            .init()
            .bindData(data)

    class LatestSongView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
        : RelativeLayout(context, attrs, defStyleAttr) {

        lateinit var title: TextView
        lateinit var recyclerView: RecyclerView
        lateinit var adapter: OnlineSongListAdapter
        lateinit var fragment: OnlineMainFragment

        init {
            initView()
        }

        private fun initView() {
            View.inflate(context, R.layout.daily_hot_song_vh, this)
            layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            recyclerView = findViewById(R.id.online_song_list)
            title = findViewById(R.id.text)
            recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        fun setCallBackFragment(f: OnlineMainFragment): LatestSongView {
            fragment = f
            return this
        }

        fun init(): LatestSongView {
            adapter = OnlineSongListAdapter(fragment)
            recyclerView.adapter = adapter
            return this
        }

        fun bindData(cell: LatestSongModel) :LatestSongView{
            if (cell.songList.isNullOrEmpty()) {
                return this
            }
            adapter.bindData(cell.songList)
            return this
        }
    }
}