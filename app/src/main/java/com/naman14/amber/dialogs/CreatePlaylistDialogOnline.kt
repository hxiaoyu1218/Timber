package com.naman14.amber.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.naman14.amber.AmberApp
import com.naman14.amber.fragments.PlaylistFragment
import com.naman14.amber.models.Song
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
        return MaterialDialog.Builder(activity).positiveText("Create").negativeText("Cancel").input("Enter playlist name", "", false) { dialog, input ->

            ServiceClient.createNewPlayList(AmberApp.getInstance().id, input.toString(), object : Callback<String> {
                override fun success(t: String?, response: Response?) {
                    val o = JSONObject(t)
                    if (o.optString("result") == "success") {
                        Toast.makeText(AmberApp.getInstance(), "Created playlist", Toast.LENGTH_SHORT).show()
                        if (parentFragment is PlaylistFragment) {
                            (parentFragment as PlaylistFragment).updateListForce()
                        }
                    }
                }

                override fun failure(error: RetrofitError?) {

                }
            })

        }.build()
    }

    companion object {

        @JvmOverloads
        fun newInstance(song: Song? = null): CreatePlaylistDialogOnline {
            val songs: LongArray
            if (song == null) {
                songs = LongArray(0)
            } else {
                songs = LongArray(1)
                songs[0] = song.id
            }
            return newInstance(songs)
        }

        fun newInstance(songList: LongArray): CreatePlaylistDialogOnline {
            val dialog = CreatePlaylistDialogOnline()
            val bundle = Bundle()
            bundle.putLongArray("songs", songList)
            dialog.arguments = bundle
            return dialog
        }
    }
}
