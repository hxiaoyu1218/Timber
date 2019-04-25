package com.naman14.amber.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.naman14.amber.MusicPlayer
import com.naman14.amber.dataloaders.PlaylistLoader
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
        return MaterialDialog.Builder(activity).title("Add to playlist").items(*chars).itemsCallback(MaterialDialog.ListCallback { dialog, itemView, which, text ->
            val songs = arguments.getLongArray("songs")
            if (which == 0) {
                CreatePlaylistDialog.newInstance(songs).show(activity.supportFragmentManager, "CREATE_PLAYLIST")
                return@ListCallback
            }

            MusicPlayer.addToPlaylist(activity, songs!!, playlists[which - 1].id)
            dialog.dismiss()
        }).build()
    }

    companion object {

        fun newInstance(song: Song): AddPlaylistDialogOnline {
            val songs = LongArray(1)
            songs[0] = song.id
            return newInstance(songs)
        }

        fun newInstance(songList: LongArray): AddPlaylistDialogOnline {
            val dialog = AddPlaylistDialogOnline()
            val bundle = Bundle()
            bundle.putLongArray("songs", songList)
            dialog.arguments = bundle
            return dialog
        }
    }
}
