package com.naman14.amber.helpers

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/21
 **/

data class SongModel(@SerializedName("song_id") var id: String,
                     @SerializedName("song_name") var name: String,
                     @SerializedName("album_id") var albumId: String,
                     @SerializedName("artist_name") var artistName: String,
                     @SerializedName("song_duration") var duration: Int,
                     @SerializedName("album_name") var albumName: String,
                     @SerializedName("artist_id") var artistId: String) : Parcelable {
    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(name)
        dest?.writeString(if (albumId.isNullOrBlank()) "" else albumId)
        dest?.writeString(artistName)
        dest?.writeInt(duration)
        dest?.writeString(albumName)
        dest?.writeString(artistId)
    }


    override fun describeContents() = 0

    constructor() : this("", "", "", "", 0, "", "")

    constructor(parcel: Parcel) : this(
            id = parcel.readString(),
            name = parcel.readString(),
            albumId = parcel.readString(),
            artistName = parcel.readString(),
            duration = parcel.readInt(),
            albumName = parcel.readString(),
            artistId = parcel.readString())

    fun readFromParcel(parcel: Parcel) {
        id = parcel.readString()
        name = parcel.readString()
        albumId = parcel.readString()
        artistName = parcel.readString()
        duration = parcel.readInt()
        albumName = parcel.readString()
        artistId = parcel.readString()
    }

    companion object CREATOR : Parcelable.Creator<SongModel> {
        override fun createFromParcel(source: Parcel): SongModel {
            return SongModel(source)
        }

        override fun newArray(size: Int): Array<SongModel> {
            return newArray(size)
        }
    }
}