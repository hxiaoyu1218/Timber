/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.amber.dataloaders

import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.PlaylistsColumns
import com.google.gson.Gson
import com.naman14.amber.AmberApp

import com.naman14.amber.models.Playlist
import com.naman14.amber.services.PlayList
import com.naman14.amber.services.PlayListSet
import com.naman14.amber.services.ServiceClient
import com.naman14.amber.utils.AmberUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

import java.util.ArrayList

interface Callback {
    fun cb(list: List<Playlist>?)
}

object PlaylistLoader {

    var mPlaylistList: ArrayList<Playlist>? = null
    var onlineList: ArrayList<Playlist> = ArrayList()
    private var mCursor: Cursor? = null
    private var hasLoad = false

    fun forceRefreshList(context: Context, defaultIncluded: Boolean, cb: com.naman14.amber.dataloaders.Callback) {
        //getLocalList(context)
        ServiceClient.getUserPlayListOnline(AmberApp.getInstance().id, object : Callback<String> {
            override fun success(t: String?, response: Response?) {
                val data = Gson().fromJson<PlayListSet>(t, PlayListSet::class.java)

                data?.let {
                    onlineList.clear()
                    for (item: PlayList in it.playLists!!.iterator()) {
                        val list = Playlist(item.listId, item.listName, item.listCount, true, item.listShareCount, item.listPic, item.isOrigin == 1)
                        onlineList.add(list)
                    }
                }
                hasLoad = true
                cb.cb(getPlaylists(context, defaultIncluded))
            }

            override fun failure(error: RetrofitError?) {
                hasLoad = true
                cb.cb(getPlaylists(context, defaultIncluded))
            }
        })
    }

    fun getPlaylists(context: Context, defaultIncluded: Boolean): List<Playlist>? {
        if (hasLoad) {
            getLocalList(context)
            mPlaylistList?.addAll(onlineList)
            if (defaultIncluded) {
                return mPlaylistList
            } else {
                return mPlaylistList?.filter {
                    (it.id >= 0 && !it.isOnline) || it.isOnline
                }
            }
        }

        return mPlaylistList
    }

    fun loadPlayList(context: Context) {
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                getLocalList(context)
            }
            ServiceClient.getUserPlayListOnline(AmberApp.getInstance().id, object : Callback<String> {
                override fun success(t: String?, response: Response?) {
                    val data = Gson().fromJson<PlayListSet>(t, PlayListSet::class.java)

                    data?.let {
                        onlineList.clear()
                        for (item: PlayList in it.playLists!!.iterator()) {
                            val list = Playlist(item.listId, item.listName, item.listCount, true, item.listShareCount, item.listPic, item.isOrigin == 1)
                            mPlaylistList?.add(list)
                            onlineList.add(list)
                        }
                    }
                    hasLoad = true
                }

                override fun failure(error: RetrofitError?) {
                    hasLoad = true
                }
            })
        }
    }

    private fun getLocalList(context: Context) {
        mPlaylistList = ArrayList()
        makeDefaultPlaylists(context)
        mCursor = makePlaylistCursor(context)
        if (mCursor != null && mCursor!!.moveToFirst()) {
            do {
                val id = mCursor!!.getLong(0)

                val name = mCursor!!.getString(1)

                val songCount = AmberUtils.getSongCountForPlaylist(context, id)

                val playlist = Playlist(id, name, songCount)

                mPlaylistList?.add(playlist)
            } while (mCursor!!.moveToNext())
        }
        if (mCursor != null) {
            mCursor!!.close()
            mCursor = null
        }
    }

    private fun makeDefaultPlaylists(context: Context) {
        val resources = context.resources

        /* Last added list */
        val lastAdded = Playlist(AmberUtils.PlaylistType.LastAdded.mId,
                resources.getString(AmberUtils.PlaylistType.LastAdded.mTitleId), -1)
        mPlaylistList?.add(lastAdded)

        /* Recently Played */
        val recentlyPlayed = Playlist(AmberUtils.PlaylistType.RecentlyPlayed.mId,
                resources.getString(AmberUtils.PlaylistType.RecentlyPlayed.mTitleId), -1)
        mPlaylistList?.add(recentlyPlayed)

        /* Top Tracks */
        val topTracks = Playlist(AmberUtils.PlaylistType.TopTracks.mId,
                resources.getString(AmberUtils.PlaylistType.TopTracks.mTitleId), -1)
        mPlaylistList?.add(topTracks)
    }


    private fun makePlaylistCursor(context: Context): Cursor? {
        return context.contentResolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                arrayOf(BaseColumns._ID, PlaylistsColumns.NAME), null, null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER)
    }

    fun deletePlaylists(context: Context, playlistId: Long) {
        val localUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
        val localStringBuilder = StringBuilder()
        localStringBuilder.append("_id IN (")
        localStringBuilder.append(playlistId)
        localStringBuilder.append(")")
        context.contentResolver.delete(localUri, localStringBuilder.toString(), null)
    }
}
