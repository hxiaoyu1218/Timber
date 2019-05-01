package com.naman14.amber.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.naman14.amber.R
import com.naman14.amber.helpers.SongModel
import com.naman14.amber.services.SearchRes
import com.naman14.amber.services.ServiceClient
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response


/**
 *  Created by huangxiaoyu on 2019/5/1
 *
 */

class SearchOnlineAdapter(context: Activity) : OnlineSongListAdapter(context) {

    val load = SongModel()
    var offset = 0
    var count = 15
    var query = ""

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            return LoadVH(
                LayoutInflater.from(parent!!.context).inflate(
                    R.layout.search_load_vh,
                    null
                )
            )
        }
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        if (mData[position].id.isEmpty()) {
            return 1
        }
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is LoadVH) {
            ServiceClient.search(
                query,
                offset.toString(),
                count.toString(),
                object : Callback<String> {
                    override fun success(t: String?, response: Response?) {
                        val res = Gson().fromJson(t, SearchRes::class.java)
                        if (res.offset == offset) {
                            mData.remove(load)
                            notifyDataSetChanged()
                            return
                        } else {
                            offset = res.offset
                            addData(res.songs)
                        }
                    }

                    override fun failure(error: RetrofitError?) {

                    }
                })
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    fun addData(data: ArrayList<SongModel>) {
        mData.remove(load)
        mData.addAll(data)
        mData.add(load)
        notifyDataSetChanged()
    }

    class LoadVH(view: View) : RecyclerView.ViewHolder(view)
}