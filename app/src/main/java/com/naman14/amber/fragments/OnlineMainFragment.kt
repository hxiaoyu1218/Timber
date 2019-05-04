package com.naman14.amber.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.afollestad.appthemeengine.Config
import com.naman14.amber.AmberApp
import com.naman14.amber.R
import com.naman14.amber.activities.MainActivity
import com.naman14.amber.coordinatescroll.CoordinateScrollLinearLayout
import com.naman14.amber.modules.ArtistModule
import com.naman14.amber.modules.DailySongModule
import com.naman14.amber.modules.LatestSongModule
import com.naman14.amber.modules.PlayListModule
import com.naman14.amber.services.*
import com.naman14.amber.utils.ATEUtils
import com.naman14.amber.utils.Helpers
import com.naman14.amber.utils.NavigationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response


/**
 *   Created by huangxiaoyu
 *   Time 2019/4/20
 **/
class OnlineMainFragment : Fragment() {

    init {
        val a = 3
    }

    private lateinit var scrollView: CoordinateScrollLinearLayout

    private var data: MainPageModel = MainPageModel()

    var isDark = false

    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater!!.inflate(
            R.layout.fragment_online_song_list, container, false
        )
        scrollView = rootView.findViewById(R.id.fragment_online_scroll_view)
        val navigationIc = rootView.findViewById<ImageView>(R.id.title_left_menu)
        val settingIc = rootView.findViewById<ImageView>(R.id.title_right_setting)
        val searchIc = rootView.findViewById<ImageView>(R.id.title_right_search_ic)
        searchIc.setOnClickListener {
            NavigationUtils.navigateOnlineSearch(activity)
        }
        navigationIc.setOnClickListener {
            (activity as MainActivity).openDrawer()
        }
        settingIc.setOnClickListener {
            NavigationUtils.navigateToSettings(activity)
        }
        isDark = PreferenceManager.getDefaultSharedPreferences(getActivity())
            .getBoolean("dark_theme", false)
        if (isDark) {
            searchIc.setImageResource(R.drawable.ic_search)
            navigationIc.setImageResource(R.drawable.ic_menu)
            settingIc.setImageResource(R.drawable.ic_setting)
        } else {
            searchIc.setImageResource(R.drawable.ic_search_dark)
            navigationIc.setImageResource(R.drawable.ic_menu_dark)
            settingIc.setImageResource(R.drawable.ic_setting_dark)
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                ServiceClient.getMainPage(AmberApp.getInstance().id, object : Callback<String> {
                    override fun success(t: String?, response: Response?) {
                        t?.let {
                            data.extract(it)
                            for (item: MainPageAbsModule in data.modules) {
                                if (item.type == 0) {
                                    val model = DailySongModule(
                                        this@OnlineMainFragment,
                                        item as DailySongModel
                                    )
                                    scrollView.addView(model.view)
                                } else if (item.type == 1) {
                                    val model = LatestSongModule(
                                        this@OnlineMainFragment,
                                        item as LatestSongModel
                                    )
                                    scrollView.addView(model.view)
                                } else if (item.type == 2) {
                                    val model = PlayListModule(
                                        this@OnlineMainFragment,
                                        item as PlayListModel
                                    )
                                    scrollView.addView(model.view)
                                } else if (item.type == 3) {
                                    val model = ArtistModule(
                                        this@OnlineMainFragment,
                                        item as ArtistListModel
                                    )
                                    scrollView.addView(model.view)
                                }
                            }
                        }
                    }

                    override fun failure(error: RetrofitError?) {

                    }
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val ateKey = Helpers.getATEKey(activity)
        ATEUtils.setStatusBarColor(activity, ateKey, Config.primaryColor(activity, ateKey))
    }
}