package com.naman14.amber.services

import com.google.gson.annotations.SerializedName
import com.naman14.amber.helpers.SongModel

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/20
 **/

data class SongListModel(@SerializedName("count") var count: Int,
                         @SerializedName("list") var songList: List<SongModel>)