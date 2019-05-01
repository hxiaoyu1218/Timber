package com.naman14.amber.services

import com.google.gson.annotations.SerializedName
import com.naman14.amber.helpers.SongModel


/**
 *  Created by huangxiaoyu on 2019/5/1
 *
 */

data class SearchRes(@SerializedName("offset") var offset: Int, @SerializedName("songs") var songs: ArrayList<SongModel>)