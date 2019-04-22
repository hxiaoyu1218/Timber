package com.naman14.amber.services

import com.naman14.amber.lastfmapi.RestServiceFactory
import retrofit.Callback
import retrofit.http.GET

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/20
 **/

interface ServiceApi {
    @GET("/music_list")
    fun getMusicList(callback: Callback<SongListModel>)
}


object ServiceClient {
    const val SERVICE_URL = "http://10.206.16.144:5000"
    private val service = RestServiceFactory.create(SERVICE_URL, ServiceApi::class.java)

    fun getSongList(callBack: Callback<SongListModel>) {
        service.getMusicList(callBack)
    }

}