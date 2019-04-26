package com.naman14.amber.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.naman14.amber.AmberApp
import com.naman14.amber.dataloaders.PlaylistLoader
import com.naman14.amber.helpers.SongModel
import com.naman14.amber.services.ServiceClient
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response


/**
 *   Created by huangxiaoyu
 *   Time 2019/4/25
 **/

class AddPlaylistDialogOnline : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val playlists = PlaylistLoader.getPlaylists(activity, false)?.filter { it.isOnline }
        val chars = arrayOfNulls<CharSequence>(playlists!!.size + 1)
        chars[0] = "Create new playlist"

        for (i in playlists.indices) {
            chars[i + 1] = playlists[i].name
        }
        return MaterialDialog.Builder(activity).title("Add to playlist").items(*chars)
            .itemsCallback(MaterialDialog.ListCallback { dialog, _, which, _ ->
                val songs = arguments.getParcelableArrayList<SongModel>("songs")
                if (which == 0) {
                    CreatePlaylistDialogOnline.newInstance(songs)
                        .show(activity.supportFragmentManager, "CREATE_PLAYLIST")
                    return@ListCallback
                }
                val map = HashMap<String, Any>()
                map.put("action_type", 1)
                map.put("list_id", playlists[which - 1].onlineId)
                val list = ArrayList<String>()
                for (item in songs) {
                    list.add(item.id)
                }
                map.put("songs", list)
                ServiceClient.listAction(ServiceClient.getJsonObject(map), object : Callback<String> {
                    override fun success(t: String?, response: Response?) {
                        //h
                        Toast.makeText(
                            AmberApp.getInstance(),
                            "Added",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    }

                    override fun failure(error: RetrofitError?) {
                        dialog.dismiss()
                    }
                })

            }).build()
    }

    companion object {

        fun newInstance(song: SongModel): AddPlaylistDialogOnline {
            val songs = arrayListOf(song)
            return newInstance(songs)
        }

        fun newInstance(songList: ArrayList<SongModel>): AddPlaylistDialogOnline {
            val dialog = AddPlaylistDialogOnline()
            val bundle = Bundle()
            bundle.putParcelableArrayList("songs", songList)
            dialog.arguments = bundle
            return dialog
        }
    }
}
