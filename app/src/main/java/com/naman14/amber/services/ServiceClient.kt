package com.naman14.amber.services

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.naman14.amber.AmberApp
import com.naman14.amber.lastfmapi.RestServiceFactory
import retrofit.Callback
import retrofit.http.*

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
    fun getMainPage(@Query("user_id") id: String, callback: Callback<String>)

    @GET("/device_regist")
    fun registDevice(@Query("user_id") id: String, callback: Callback<String>)

    @GET("/user_play_list")
    fun getPlayListOnline(@Query("user_id") id: String, callback: Callback<String>)

    @GET("/list_upload")
    fun createNewPlayList(@Query("user_id") uid: String, @Query("list_name") name: String, callback: Callback<String>)

    @Headers("Content-Type: application/json")
    @POST("/list_action")
    fun listAction(@Body body: JsonObject, callback: Callback<String>)

    @GET("/music_event")
    fun musicEvent(@Query("user_id") uid: String, @Query("song_id") songId: String, callback: Callback<String>)

    @GET("/search")
    fun search(
        @Query("user_id") uid: String,
        @Query("query") query: String, @Query("offset") offset: String, @Query("count") count: String, @Query(
            "artist"
        ) artist: String,
        callback: Callback<String>
    )

    @Headers("Content-Type: application/json")
    @POST("/user_login")
    fun userLogin(@Body body: JsonObject, callback: Callback<String>)

    @Headers("Content-Type: application/json")
    @POST("/user_regist")
    fun userRegister(@Body body: JsonObject, callback: Callback<String>)

    @GET("/artist_content")
    fun getArtistContent(@Query("artist_id") id: String, callback: Callback<String>)


}

interface ServiceResApi {
    @GET("/lrc")
    fun getMusicLRC(@Query("song_id") id: String, callback: Callback<String>)
}


object ServiceClient {
    const val RES_SERVICE_URL = "http://10.206.16.144:3000"
    const val SERVICE_URL = "http://10.206.16.144:5000"
    private val service = RestServiceFactory.create(SERVICE_URL, ServiceApi::class.java)
    private val resService = RestServiceFactory.create(RES_SERVICE_URL, ServiceResApi::class.java)
    private val gson = GsonBuilder().serializeNulls().create()
    private val jsonParser = JsonParser()

    fun getJsonObject(map: Map<String, Any>): JsonObject {
        val j = gson.toJson(map)
        return jsonParser.parse(j).asJsonObject
    }

    fun getSongList(callBack: Callback<String>) {
        service.getMusicList(callBack)
    }

    fun getMainPage(id: String, callBack: Callback<String>) {
        service.getMainPage(id, callBack)
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

    fun listAction(body: JsonObject, callback: Callback<String>) {
        service.listAction(body, callback)
    }

    fun musicEvent(uid: String, songId: String, callback: Callback<String>) {
        service.musicEvent(uid, songId, callback)
    }

    fun search(
        query: String,
        offset: String,
        count: String,
        artist: String,
        callback: Callback<String>
    ) {
        service.search(AmberApp.getInstance().id, query, offset, count, artist, callback)
    }

    fun userLogin(body: JsonObject, callback: Callback<String>) {
        service.userLogin(body, callback)
    }

    fun userRegister(body: JsonObject, callback: Callback<String>) {
        service.userRegister(body, callback)
    }

    fun getArtitContent(id: String, callback: Callback<String>) {
        service.getArtistContent(id, callback)
    }

    fun getMusicLRC(id: String, callback: Callback<String>) {
        resService.getMusicLRC(id, callback)
    }
}