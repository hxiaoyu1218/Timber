package com.naman14.amber.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.naman14.amber.MusicPlayer
import com.naman14.amber.R.string.songs
import com.naman14.amber.dataloaders.PlaylistLoader
import com.naman14.amber.helpers.SongModel
import com.naman14.amber.models.Song

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/25
 **/

class AddPlaylistDialogOnline : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val playlists = PlaylistLoader.getPlaylists(activity, false)
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
                //server
                dialog.dismiss()
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
