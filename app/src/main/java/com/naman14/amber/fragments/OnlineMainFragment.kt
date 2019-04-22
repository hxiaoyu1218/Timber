package com.naman14.amber.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.naman14.amber.R
import com.naman14.amber.adapters.OnlineSongListAdapter
import com.naman14.amber.services.ServiceClient
import com.naman14.amber.services.SongListModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response


/**
 *   Created by huangxiaoyu
 *   Time 2019/4/20
 **/
class OnlineMainFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OnlineSongListAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(
                R.layout.fragment_online_song_list, container, false)
        adapter = OnlineSongListAdapter(this)
        recyclerView = rootView.findViewById(R.id.online_song_list)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter


        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                ServiceClient.getSongList(object : Callback<SongListModel> {
                    override fun success(t: SongListModel?, response: Response?) {
                        t?.let {
                            adapter.bindData(it)
                        }
                    }

                    override fun failure(error: RetrofitError?) {

                    }
                })
            }
        }

        return rootView
    }

}