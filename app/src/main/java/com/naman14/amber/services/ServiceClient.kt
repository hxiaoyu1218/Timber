package com.naman14.amber.services

import com.naman14.amber.lastfmapi.RestServiceFactory
import retrofit.Callback
import retrofit.http.GET
import retrofit.http.Query

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/20
 **/

interface ServiceApi {
    @GET("/music_list")
    fun getMusicList(callback: Callback<String>)

    @GET("/list_content")
    fun getListContent(@Query("list_id") uid: String, callback: Callback<String>)

    @GET("/main_page")
    fun getMainPage(callback: Callback<String>)

    @GET("/user_regist")
    fun registDevice(@Query("user_id") id: String, callback: Callback<String>)

    @GET("/user_play_list")
    fun getPlayListOnline(@Query("user_id") id: String, callback: Callback<String>)

    @GET("/list_upload")
    fun createNewPlayList(@Query("user_id") uid: String, @Query("list_name") name: String, callback: Callback<String>)
}


object ServiceClient {
    const val SERVICE_URL = "http://10.206.16.144:5000"
    private val service = RestServiceFactory.create(SERVICE_URL, ServiceApi::class.java)

    fun getSongList(callBack: Callback<String>) {
        service.getMusicList(callBack)
    }

    fun getMainPage(callBack: Callback<String>) {
        service.getMainPage(callBack)
    }

    fun registDevice(id: String, callBack: Callback<String>) {
        service.registDevice(id, callBack)
    }

    fun getUserPlayListOnline(id: String, callBack: Callback<String>) {
        service.getPlayListOnline(id, callBack)
    }

    fun createNewPlayList(uid: String, name: String, callback: Callback<String>) {
        service.createNewPlayList(uid, name, callback)
    }

    fun getListContent(id: String, callback: Callback<String>) {
        service.getListContent(id, callback)
    }

}