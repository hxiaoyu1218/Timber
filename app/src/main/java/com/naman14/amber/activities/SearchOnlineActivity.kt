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
import com.naman14.amber.adapters.SearchOnlineAdapter
import com.naman14.amber.services.SearchRes
import com.naman14.amber.services.ServiceClient
import com.naman14.amber.utils.AmberUtils
import com.naman14.amber.utils.PreferencesUtility
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


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        songListAdapter = SearchOnlineAdapter(this@SearchOnlineActivity)
        recyclerView.adapter = songListAdapter

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
        ServiceClient.search(query, "0", songListAdapter.count.toString(), object : Callback<String> {
            override fun success(t: String?, response: Response?) {
                val res = Gson().fromJson(t, SearchRes::class.java)
                songListAdapter.offset = res.offset
                songListAdapter.query = query
                if (res.songs.size < songListAdapter.count) {
                    songListAdapter.bindData(res.songs)
                } else {
                    res.songs.add(songListAdapter.load)
                    songListAdapter.bindData(res.songs)
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
}