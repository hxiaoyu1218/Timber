package com.naman14.amber.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.appthemeengine.Config
import com.naman14.amber.R
import com.naman14.amber.fragments.OnlineMainFragment
import com.naman14.amber.services.PlayList
import com.naman14.amber.services.ServiceClient
import com.naman14.amber.utils.Helpers
import com.naman14.amber.utils.NavigationUtils
import com.naman14.amber.utils.UIUtils
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/23
 **/
class PlayListsAdapter(val f: OnlineMainFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = ArrayList<PlayList>(6)
    private val ateKey = Helpers.getATEKey(f.context)
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return PlayListViewHolder(
            this,
            LayoutInflater.from(parent!!.context).inflate(
                R.layout.play_list_item_vh,
                null
            )
        )
    }

    override fun getItemCount() = data.count()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as PlayListViewHolder).bind(data[position])
    }

    fun bindData(list: List<PlayList>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    class PlayListViewHolder(val adapter: PlayListsAdapter, view: View) :
        RecyclerView.ViewHolder(view) {

        var cover: ImageView = view.findViewById(R.id.play_list_cover)
        var name: TextView = view.findViewById(R.id.play_list_name)
        var data: PlayList? = null

        init {
            val w = UIUtils.getScreenWidth(view.context)
            val rw = (w - 4 * UIUtils.dip2Px(view.context, 16)) / 3
            UIUtils.setLayoutParams(cover, rw.toInt(), rw.toInt())
            itemView.setOnClickListener {
                NavigationUtils.navigateOnlinePlayList(
                    itemView.context,
                    data
                )
            }
        }


        fun bind(data: PlayList) {
            this.data = data
            name.text = data.listName
            name.setTextColor(Config.textColorPrimary(adapter.f.context, adapter.ateKey))
            ImageLoader.getInstance().displayImage(
                ServiceClient.RES_SERVICE_URL + "/album_pic?song_id=" + data.listPic,
                cover, DisplayImageOptions.Builder().cacheInMemory(true)
                    .showImageOnLoading(R.drawable.holder)
                    .resetViewBeforeLoading(true).build()
            )
        }
    }
}