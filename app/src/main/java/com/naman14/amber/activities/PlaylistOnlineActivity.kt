package com.naman14.amber.activities

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.afollestad.appthemeengine.ATE
import com.google.gson.Gson
import com.naman14.amber.AmberApp
import com.naman14.amber.R
import com.naman14.amber.R.string.playlists
import com.naman14.amber.R.string.songs
import com.naman14.amber.adapters.OnlineSongListAdapter
import com.naman14.amber.dataloaders.Callback
import com.naman14.amber.dataloaders.PlaylistLoader
import com.naman14.amber.models.Playlist
import com.naman14.amber.services.ServiceClient
import com.naman14.amber.services.SongListModel
import com.naman14.amber.utils.AmberUtils
import com.naman14.amber.utils.Constants
import com.naman14.amber.utils.PreferencesUtility
import retrofit.RetrofitError
import retrofit.client.Response


/**
 *  Created by huangxiaoyu on 2019/4/29
 *
 */

class PlaylistOnlineActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OnlineSongListAdapter
    private val playlist = Playlist()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        if (PreferencesUtility.getInstance(this).theme == "dark") {
            setTheme(R.style.AppThemeDark)
        }
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false)) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        } else {
            if (AmberUtils.isMarshmallow()) {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            } else {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_playlist_detai_onlinel)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)


        val title = intent.getStringExtra(Constants.PLAY_LIST_NAME)
        val id = intent.getStringExtra(Constants.PLAY_LIST_ID)
        playlist.onlineId = id
        playlist.name = title

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = title


        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = OnlineSongListAdapter(this@PlaylistOnlineActivity)
        recyclerView.adapter = adapter

        ServiceClient.getListContent(id, object : retrofit.Callback<String> {
            override fun success(t: String?, response: Response?) {
                val data = Gson().fromJson<SongListModel>(t, SongListModel::class.java)
                adapter.bindData(data.songList)
            }

            override fun failure(error: RetrofitError?) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list_online, menu)
        ATE.applyMenu(this, ateKey, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            if (it.itemId == R.id.save) {
                val map = HashMap<String, Any>()
                map.put("action_type", 3)
                map.put("list_id", playlist.onlineId)
                map.put("user_id", AmberApp.getInstance().id)
                ServiceClient.listAction(
                    ServiceClient.getJsonObject(map),
                    object : retrofit.Callback<String> {
                        override fun success(t: String?, response: Response?) {
                            //h
                            Toast.makeText(
                                AmberApp.getInstance(),
                                "Saved",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun failure(error: RetrofitError?) {
                            Toast.makeText(
                                AmberApp.getInstance(),
                                "Saved Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            } else if (it.itemId == android.R.id.home) {
                finish()
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }
}