package com.naman14.amber.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.appthemeengine.Config
import com.naman14.amber.MusicPlayer
import com.naman14.amber.R
import com.naman14.amber.fragments.OnlineMainFragment
import com.naman14.amber.helpers.SongModel
import com.naman14.amber.services.ServiceClient
import com.naman14.amber.services.SongListModel
import com.naman14.amber.utils.Helpers
import com.naman14.amber.utils.NavigationUtils
import com.naman14.amber.widgets.MusicVisualizer
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/22
 **/
class OnlineMainPageAdapter(val f: OnlineMainFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val mData = ArrayList<SongModel>()
    val ateKey = Helpers.getATEKey(f.context)
    var currentlyPlayingPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return OnlineSongViewHolder(
            this,
            LayoutInflater.from(parent!!.context).inflate(
                R.layout.online_song_vh,
                null
            )
        )
    }

    override fun getItemCount() = mData.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as OnlineSongViewHolder).bind(mData[position])
    }

    fun bindData(model: SongListModel) {
        mData.clear()
        mData.addAll(model.songList)
        notifyDataSetChanged()
    }

    class OnlineSongViewHolder(val adapter: OnlineMainPageAdapter, view: View) :
        RecyclerView.ViewHolder(view) {

        var title = view.findViewById<View>(R.id.song_title) as TextView
        var artist = view.findViewById<View>(R.id.song_artist) as TextView
        var albumArt = view.findViewById<View>(R.id.albumArt) as ImageView
        var popupMenu = view.findViewById<View>(R.id.popup_menu) as ImageView
        var visualizer = view.findViewById<View>(R.id.visualizer) as MusicVisualizer

        var data: SongModel? = null

        init {
            itemView.setOnClickListener {
                data?.let {
                    NavigationUtils.navigateToNowplayingOnline(adapter.f.activity)
                    MusicPlayer.playOnline(it)
                    adapter.notifyItemChanged(adapter.currentlyPlayingPosition)
                    adapter.currentlyPlayingPosition = adapterPosition
                    title.setTextColor(Config.accentColor(adapter.f.context, adapter.ateKey))
                    visualizer.setColor(Config.accentColor(adapter.f.context, adapter.ateKey))
                    visualizer.visibility = View.VISIBLE
                }
            }
        }

        fun bind(data: SongModel) {
            this.data = data
            title.text = data.name
            artist.text = data.artistName
            albumArt.setImageResource(R.drawable.holder)
            title.setTextColor(Config.textColorPrimary(adapter.f.context, adapter.ateKey))

            if (MusicPlayer.getCurrentOnlineId() == data.id) {
                title.setTextColor(Config.accentColor(adapter.f.context, adapter.ateKey))
                if (MusicPlayer.isOnlinePlaying()) {
                    visualizer.setColor(Config.accentColor(adapter.f.context, adapter.ateKey))
                    visualizer.visibility = View.VISIBLE
                } else {
                    visualizer.visibility = View.GONE
                }
            } else {
                visualizer.visibility = View.GONE

                title.setTextColor(Config.textColorPrimary(adapter.f.context, adapter.ateKey))

            }

            ImageLoader.getInstance().displayImage(
                ServiceClient.SERVICE_URL + "/album_pic?song_id=" + data.id,
                albumArt, DisplayImageOptions.Builder().cacheInMemory(true)
                    .showImageOnLoading(R.drawable.holder)
                    .resetViewBeforeLoading(true).build()
            )

        }
    }

}