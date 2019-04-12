package com.naman14.amber.activities

import android.graphics.Color
import android.os.Bundle
import com.afollestad.appthemeengine.Config
import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer
import com.afollestad.appthemeengine.customizers.ATEStatusBarCustomizer
import com.afollestad.appthemeengine.customizers.ATEToolbarCustomizer
import com.naman14.amber.R
import com.naman14.amber.fragments.ArtistDetailFragment
import com.naman14.amber.utils.Constants

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/12
 **/
class ArtistDetailActivity : BaseActivity() , ATEActivityThemeCustomizer, ATEToolbarCustomizer, ATEStatusBarCustomizer {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_detail)
        val id = intent.getLongExtra(Constants.ARTIST_ID, 0L)
        val t = supportFragmentManager.beginTransaction()
        t.add(R.id.fragment_container, ArtistDetailFragment.newInstance(id, false, null)).commit()
    }

    override fun getActivityTheme(): Int {
        return R.style.PlayerThemeFullScreen
    }

    override fun getToolbarColor(): Int {
        return Color.TRANSPARENT
    }

    override fun getStatusBarColor(): Int {
        return Color.TRANSPARENT
    }

    override fun getLightToolbarMode(): Int {
        return Config.LIGHT_TOOLBAR_AUTO
    }

    override fun getLightStatusBarMode(): Int {
        return Config.LIGHT_STATUS_BAR_OFF
    }
}