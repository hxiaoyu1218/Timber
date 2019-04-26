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
import com.naman14.amber.fragments.PlaylistFragment
import com.naman14.amber.helpers.SongModel
import com.naman14.amber.services.ServiceClient
import org.json.JSONObject
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/25
 **/

class CreatePlaylistDialogOnline : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog.Builder(activity).positiveText("Create").negativeText("Cancel")
            .input("Enter playlist name", "", false) { dialog, input ->

                ServiceClient.createNewPlayList(
                    AmberApp.getInstance().id,
                    input.toString(),
                    object : Callback<String> {
                        override fun success(t: String?, response: Response?) {
                            val o = JSONObject(t)
                            if (o.optString("result") == "success") {
                                Toast.makeText(
                                    AmberApp.getInstance(),
                                    "Created playlist",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val listId = o.optString("list_id")
                                val songs = arguments.getParcelableArrayList<SongModel>("songs")
                                if (!songs.isNullOrEmpty()) {
                                    //server add new song to list
                                    //call back to update list
                                    val map = HashMap<String, Any>()
                                    map.put("action_type", 1)
                                    map.put("list_id", listId)
                                    val list = ArrayList<String>()
                                    for (item in songs) {
                                        list.add(item.id)
                                    }
                                    map.put("songs", list)
                                    ServiceClient.listAction(
                                        ServiceClient.getJsonObject(map),
                                        object : Callback<String> {
                                            override fun success(t: String?, response: Response?) {
                                                //h
                                                if (parentFragment is PlaylistFragment) {
                                                    (parentFragment as PlaylistFragment).updateListForce()
                                                }
                                            }

                                            override fun failure(error: RetrofitError?) {}
                                        })

                                } else {
                                    if (parentFragment is PlaylistFragment) {
                                        (parentFragment as PlaylistFragment).updateListForce()
                                    }
                                }

                            }
                        }

                        override fun failure(error: RetrofitError?) {

                        }
                    })

            }.build()
    }

    companion object {

        fun newInstance(song: SongModel): CreatePlaylistDialogOnline {
            val songs = arrayListOf(song)
            return newInstance(songs)
        }

        fun newInstance(): CreatePlaylistDialogOnline {
            val dialog = CreatePlaylistDialogOnline()
            val bundle = Bundle()
            bundle.putParcelableArrayList("songs", ArrayList())
            dialog.arguments = bundle
            return dialog
        }

        fun newInstance(songList: ArrayList<SongModel>): CreatePlaylistDialogOnline {
            val dialog = CreatePlaylistDialogOnline()
            val bundle = Bundle()
            bundle.putParcelableArrayList("songs", songList)
            dialog.arguments = bundle
            return dialog
        }
    }
}
