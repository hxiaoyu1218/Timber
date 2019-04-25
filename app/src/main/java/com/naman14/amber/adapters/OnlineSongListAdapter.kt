package com.naman14.amber.adapters

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.afollestad.appthemeengine.Config
import com.naman14.amber.MusicPlayer
import com.naman14.amber.R
import com.naman14.amber.activities.BaseActivity
import com.naman14.amber.dialogs.AddPlaylistDialog
import com.naman14.amber.dialogs.AddPlaylistDialogOnline
import com.naman14.amber.helpers.SongModel
import com.naman14.amber.models.Song
import com.naman14.amber.services.ServiceClient
import com.naman14.amber.utils.AmberUtils
import com.naman14.amber.utils.Helpers
import com.naman14.amber.utils.NavigationUtils
import com.naman14.amber.widgets.MusicVisualizer
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/20
 **/
class OnlineSongListAdapter(val activity: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val mData = ArrayList<SongModel>()
    val ateKey = Helpers.getATEKey(activity)
    var currentlyPlayingPosition = 0
    var isList = false

    init {
        currentlyPlayingPosition = MusicPlayer.getCurrentPosOnline()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return OnlineSongViewHolder(
            this,
            LayoutInflater.from(parent!!.context).inflate(
                if (isList) R.layout.item_song_playlist else R.layout.online_song_vh,
                null
            )
        )
    }

    override fun getItemCount() = mData.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as OnlineSongViewHolder).bind(mData[position], position)
    }

    fun bindData(songList: List<SongModel>) {
        mData.clear()
        mData.addAll(songList)
        notifyDataSetChanged()
    }

    fun removeSong(pos: Int) {
        mData.removeAt(pos)
        notifyItemRemoved(pos)
    }

    fun addSongAt(song:SongModel, pos:Int){

    }

    class OnlineSongViewHolder(val adapter: OnlineSongListAdapter, view: View) :
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
                    NavigationUtils.navigateToNowplayingOnline(adapter.activity)
                    MusicPlayer.playOnlineWithList(adapter.mData, adapterPosition)
                    adapter.notifyItemChanged(adapter.currentlyPlayingPosition)
                    adapter.currentlyPlayingPosition = adapterPosition
                    title.setTextColor(Config.accentColor(view.context, adapter.ateKey))
                    visualizer.setColor(Config.accentColor(view.context, adapter.ateKey))
                    visualizer.visibility = View.VISIBLE
                }
            }
            popupMenu.setOnClickListener {


                val menu = PopupMenu(adapter.activity, it)
                menu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.popup_song_remove_playlist -> {
                            //server
                            adapter.removeSong(adapterPosition)
                        }
                        R.id.popup_song_play -> {

                        }
                        R.id.popup_song_play_next -> {

                        }
                        R.id.popup_song_addto_queue -> {

                        }
                        R.id.popup_song_addto_playlist -> {
                            AddPlaylistDialogOnline.newInstance(data!!)
                                .show((adapter.activity as BaseActivity).supportFragmentManager, "ADD_PLAYLIST")
                        }
                    }
                    return@setOnMenuItemClickListener false
                }
                menu.inflate(R.menu.menu_popup_song_online)
                menu.show()
                if (adapter.isList)
                    menu.menu.findItem(R.id.popup_song_remove_playlist).isVisible = true
            }
        }

        fun bind(data: SongModel, i: Int) {
            this.data = data
            title.text = data.name
            artist.text = data.artistName
            albumArt.setImageResource(R.drawable.holder)
            title.setTextColor(Config.textColorPrimary(itemView.context, adapter.ateKey))

            if (MusicPlayer.getCurrentOnlineId() == data.id) {
                title.setTextColor(Config.accentColor(itemView.context, adapter.ateKey))
                if (MusicPlayer.isOnlinePlaying()) {
                    visualizer.setColor(Config.accentColor(itemView.context, adapter.ateKey))
                    visualizer.visibility = View.VISIBLE
                } else {
                    visualizer.visibility = View.GONE
                }
            } else {
                visualizer.visibility = View.GONE
                if (adapter.isList) {
                    title.setTextColor(Color.WHITE)
                } else {
                    title.setTextColor(Config.textColorPrimary(itemView.context, adapter.ateKey))
                }


            }

            ImageLoader.getInstance().displayImage(
                ServiceClient.SERVICE_URL + "/album_pic?song_id=" + data.id,
                albumArt, DisplayImageOptions.Builder().cacheInMemory(true)
                    .showImageOnLoading(R.drawable.holder)
                    .resetViewBeforeLoading(true).build()
            )

            if (adapter.isList) {
                if (AmberUtils.isLollipop())
                    adapter.setAnimation(itemView, i)
                else {
                    if (i > 10)
                        adapter.setAnimation(itemView, i)
                }
            }

        }
    }

    private var lastPosition = -1
    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(activity, R.anim.abc_slide_in_bottom)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

}