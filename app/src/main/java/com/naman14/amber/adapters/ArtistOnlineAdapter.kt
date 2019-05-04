package com.naman14.amber.adapters

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.appthemeengine.Config
import com.naman14.amber.R
import com.naman14.amber.R.id.foreground
import com.naman14.amber.helpers.SongModel
import com.naman14.amber.services.ServiceClient
import com.naman14.amber.utils.Helpers
import com.naman14.amber.utils.NavigationUtils
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader


/**
 *  Created by huangxiaoyu on 2019/5/4
 *
 */

class ArtistOnlineAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data = ArrayList<SongModel>()
    val ateKey = Helpers.getATEKey(context)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return ArtistOnlineVH(
            this,
            LayoutInflater.from(parent!!.context).inflate(
                R.layout.artist_online_vh,
                null
            )
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as ArtistOnlineVH).bind(data[position])
    }

    fun bindData(list: List<SongModel>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    class ArtistOnlineVH(val adapter: ArtistOnlineAdapter, view: View) :
        RecyclerView.ViewHolder(view) {

        val avatar = view.findViewById<ImageView>(R.id.artist_avatar)
        val name = view.findViewById<TextView>(R.id.artist_name)
        var data: SongModel? = null

        init {
            itemView.setOnClickListener {
                data?.let {
                    val tranitionViews = java.util.ArrayList<Pair<*, *>>()
                    tranitionViews.add(
                        0,
                        Pair.create(avatar as View, "artist_avatar")
                    )
                    tranitionViews.add(
                        1,
                        Pair.create(name as View, "artist_name")
                    )
                    NavigationUtils.navigateToArtistOnline(adapter.context as Activity, it.artistId, it.artistName, tranitionViews)
                }
            }
        }

        fun bind(data: SongModel) {
            this.data = data
            name.text = data.artistName
            name.setTextColor(Config.textColorPrimary(adapter.context, adapter.ateKey))
            ImageLoader.getInstance().displayImage(
                ServiceClient.SERVICE_URL + "/artist_pic?artist_id=" + data.artistId,
                avatar, DisplayImageOptions.Builder().cacheInMemory(true)
                    .showImageOnLoading(R.drawable.holder)
                    .resetViewBeforeLoading(true).build()
            )
        }


    }
}