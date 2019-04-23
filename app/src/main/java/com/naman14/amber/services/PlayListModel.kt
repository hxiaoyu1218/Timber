package com.naman14.amber.services

import com.google.gson.annotations.SerializedName

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/23
 **/
data class PlayList(@SerializedName("list_id") var listId: String?,
                         @SerializedName("list_name") var listName: String?,
                         @SerializedName("list_pic") var listPic: String?,
                         @SerializedName("list_share_count") var listShareCount: Int){}