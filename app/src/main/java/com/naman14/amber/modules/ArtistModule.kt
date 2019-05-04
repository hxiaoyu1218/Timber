package com.naman14.amber.modules

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.naman14.amber.R
import com.naman14.amber.adapters.ArtistOnlineAdapter
import com.naman14.amber.fragments.OnlineMainFragment
import com.naman14.amber.services.ArtistListModel
import com.naman14.amber.utils.UIUtils


/**
 *  Created by huangxiaoyu on 2019/5/4
 *
 */

class ArtistModule(val f: OnlineMainFragment, val data: ArtistListModel) {
    val view = ArtistListView(f.context)
        .setCallBackFragment(f)
        .init(f.isDark)
        .bindData(data)

    class ArtistListView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : RelativeLayout(context, attrs, defStyleAttr) {

        lateinit var title: TextView
        lateinit var recyclerView: RecyclerView
        lateinit var adapter: ArtistOnlineAdapter
        lateinit var fragment: OnlineMainFragment

        init {
            initView()
        }

        private fun initView() {
            View.inflate(context, R.layout.daily_hot_song_vh, this)
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            recyclerView = findViewById(R.id.online_song_list)
            val margin = UIUtils.dip2Px(context, 8).toInt()
            UIUtils.updateLayoutMargin(recyclerView, margin, margin, margin, 0)
            title = findViewById(R.id.text)
            recyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }

        fun setCallBackFragment(f: OnlineMainFragment): ArtistListView {
            fragment = f
            return this
        }

        fun init(isDark: Boolean): ArtistListView {
            adapter = ArtistOnlineAdapter(fragment.activity)
            recyclerView.adapter = adapter
            title.setTextColor(if (isDark) Color.WHITE else Color.BLACK)
            return this
        }

        fun bindData(cell: ArtistListModel): ArtistListView {
            if (cell.artistList.isNullOrEmpty()) {
                return this
            }
            title.text = cell.title
            adapter.bindData(cell.artistList)
            return this
        }
    }
}