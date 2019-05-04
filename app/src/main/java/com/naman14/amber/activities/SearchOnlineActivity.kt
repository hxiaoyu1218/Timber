package com.naman14.amber.activities

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.afollestad.appthemeengine.ATE
import com.google.gson.Gson
import com.naman14.amber.R
import com.naman14.amber.adapters.ArtistOnlineAdapter
import com.naman14.amber.adapters.SearchOnlineAdapter
import com.naman14.amber.services.SearchRes
import com.naman14.amber.services.ServiceClient
import com.naman14.amber.utils.AmberUtils
import com.naman14.amber.utils.PreferencesUtility
import kotlinx.android.synthetic.main.activity_search_online.*
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response


/**
 *  Created by huangxiaoyu on 2019/4/30
 *
 */

class SearchOnlineActivity : BaseActivity(), SearchView.OnQueryTextListener, View.OnTouchListener {


    private lateinit var mSearchView: SearchView
    private var mImm: InputMethodManager? = null
    private lateinit var songListAdapter: SearchOnlineAdapter
    private lateinit var adapter: ArtistOnlineAdapter

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
        setContentView(R.layout.activity_search_online)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        songListAdapter = SearchOnlineAdapter(this@SearchOnlineActivity)
        recyclerView.adapter = songListAdapter

        val rv = findViewById<RecyclerView>(R.id.recyclerview_a)
        rv.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        adapter = ArtistOnlineAdapter(this)
        rv.adapter = adapter

        search_artist_text.visibility = View.GONE
        search_song_text.visibility = View.GONE

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_search, menu)

        mSearchView = MenuItemCompat.getActionView(menu.findItem(R.id.menu_search)) as SearchView

        mSearchView.setOnQueryTextListener(this)
        mSearchView.setQueryHint(getString(R.string.search_library))

        mSearchView.setIconifiedByDefault(false)
        mSearchView.setIconified(false)

        MenuItemCompat.setOnActionExpandListener(
            menu.findItem(R.id.menu_search),
            object : MenuItemCompat.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    finish()
                    return false
                }
            })

        menu.findItem(R.id.menu_search).expandActionView()

        ATE.applyMenu(this, ateKey, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
//        val item = menu.findItem(R.id.action_search)
//        item.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        ServiceClient.search(
            query,
            "0",
            songListAdapter.count.toString(),
            "1",
            object : Callback<String> {
                override fun success(t: String?, response: Response?) {
                    val res = Gson().fromJson(t, SearchRes::class.java)
                    if (!res.artists.isNullOrEmpty()) {
                        adapter.bindData(res.artists)
                        recyclerview_a.visibility = View.VISIBLE
                        search_artist_text.visibility = View.VISIBLE
                    } else {
                        recyclerview_a.visibility = View.GONE
                        search_artist_text.visibility = View.GONE
                    }
                    search_song_text.visibility =
                        if (res.songRes.songs.isNullOrEmpty()) View.GONE else View.VISIBLE
                    songListAdapter.offset = res.songRes.offset
                    songListAdapter.query = query
                    if (res.songRes.songs.size < songListAdapter.count) {
                        songListAdapter.bindData(res.songRes.songs)
                    } else {
                        res.songRes.songs.add(songListAdapter.load)
                        songListAdapter.bindData(res.songRes.songs)
                    }
                }

                override fun failure(error: RetrofitError?) {

                }
            })
        hideInputManager()

        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        return true
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        hideInputManager()
        return false
    }

    fun hideInputManager() {
        mImm?.hideSoftInputFromWindow(mSearchView.windowToken, 0)
        mSearchView.clearFocus()
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }
}