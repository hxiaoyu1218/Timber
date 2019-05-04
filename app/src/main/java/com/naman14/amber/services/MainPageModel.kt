package com.naman14.amber.services

import com.google.gson.Gson
import com.naman14.amber.helpers.SongModel
import org.json.JSONObject

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/23
 **/

open class MainPageAbsModule {
    var type = 0
    var title: String? = null
    open fun extract(jsonObject: JSONObject) {
        type = jsonObject.optInt("type", 0)
        title = jsonObject.optString("title")
    }
}

class MainPageModel {
    var modules: ArrayList<MainPageAbsModule> = ArrayList()
    fun extract(string: String) {
        val jsonObject = JSONObject(string)
        val list = jsonObject.optJSONArray("modules")
        for (i in 0..(list.length() - 1)) {
            val type = (list[i] as JSONObject).optInt("type")
            if (type == 0) {
                val module = DailySongModel()
                module.extract(list[i] as JSONObject)
                modules.add(module)
            } else if (type == 1) {
                val module = LatestSongModel()
                module.extract(list[i] as JSONObject)
                modules.add(module)
            } else if (type == 2) {
                val module = PlayListModel()
                module.extract(list[i] as JSONObject)
                modules.add(module)
            } else if (type == 3) {
                val module = ArtistListModel()
                module.extract(list[i] as JSONObject)
                modules.add(module)
            }
        }
    }
}

class ArtistListModel : MainPageAbsModule() {

    var artistList: ArrayList<SongModel> = ArrayList()

    override fun extract(jsonObject: JSONObject) {
        super.extract(jsonObject)
        val list = jsonObject.optJSONArray("artists")
        for (i in 0..(list.length() - 1)) {
            val song = Gson().fromJson(list[i].toString(), SongModel::class.java)
            artistList.add(song)
        }
    }
}

class DailySongModel : MainPageAbsModule() {

    var songList: ArrayList<SongModel> = ArrayList()

    override fun extract(jsonObject: JSONObject) {
        super.extract(jsonObject)
        val list = jsonObject.optJSONArray("songs")
        for (i in 0..(list.length() - 1)) {
            val song = Gson().fromJson(list[i].toString(), SongModel::class.java)
            songList.add(song)
        }
    }
}

class LatestSongModel : MainPageAbsModule() {

    var songList: ArrayList<SongModel> = ArrayList()

    override fun extract(jsonObject: JSONObject) {
        super.extract(jsonObject)
        val list = jsonObject.optJSONArray("songs")
        for (i in 0..(list.length() - 1)) {
            val song = Gson().fromJson(list[i].toString(), SongModel::class.java)
            songList.add(song)
        }
    }
}

class PlayListModel : MainPageAbsModule() {

    var playList: ArrayList<PlayList> = ArrayList()

    override fun extract(jsonObject: JSONObject) {
        super.extract(jsonObject)
        val list = jsonObject.optJSONArray("lists")
        for (i in 0..(list.length() - 1)) {
            val item = Gson().fromJson(list[i].toString(), PlayList::class.java)
            playList.add(item)
        }
    }
}