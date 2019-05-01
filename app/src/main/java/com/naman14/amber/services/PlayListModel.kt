package com.naman14.amber.services

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/23
 **/
data class PlayListSet(@SerializedName("play_lists") var playLists: ArrayList<PlayList>?)

data class PlayList(
    @SerializedName("list_id") var listId: String?,
    @SerializedName("list_name") var listName: String?,
    @SerializedName("list_pic") var listPic: String?,
    @SerializedName("list_count") var listCount: Int,
    @SerializedName("origin") var isOrigin: Int,
    @SerializedName("list_share_count") var listShareCount: Int
)