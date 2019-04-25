package com.naman14.amber.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.naman14.amber.R
import com.naman14.amber.activities.MainActivity
import com.naman14.amber.adapters.OnlineSongListAdapter
import com.naman14.amber.coordinatescroll.CoordinateScrollLinearLayout
import com.naman14.amber.modules.DailySongModule
import com.naman14.amber.modules.LatestSongModule
import com.naman14.amber.modules.PlayListModule
import com.naman14.amber.services.*
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

    private lateinit var scrollView: CoordinateScrollLinearLayout

    private var data: MainPageModel = MainPageModel()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(
                R.layout.fragment_online_song_list, container, false)
        scrollView = rootView.findViewById(R.id.fragment_online_scroll_view)
        val navigationIc = rootView.findViewById<ImageView>(R.id.title_left_menu)
        val settingIc = rootView.findViewById<ImageView>(R.id.title_right_setting)
        navigationIc.setOnClickListener {
            (activity as MainActivity).openDrawer()
        }
        settingIc.setOnClickListener {
            NavigationUtils.navigateToSettings(activity)
        }
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                ServiceClient.getMainPage(object : Callback<String> {
                    override fun success(t: String?, response: Response?) {
                        t?.let {
                            data.extract(it)
                            for (item: MainPageAbsModule in data.modules) {
                                if (item.type == 0) {
                                    val model = DailySongModule(this@OnlineMainFragment, item as DailySongModel)
                                    scrollView.addView(model.view)
                                } else if (item.type == 1) {
                                    val model = LatestSongModule(this@OnlineMainFragment, item as LatestSongModel)
                                    scrollView.addView(model.view)
                                } else if (item.type == 2) {
                                    val model = PlayListModule(this@OnlineMainFragment, item as PlayListModel)
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

        return rootView
    }

}