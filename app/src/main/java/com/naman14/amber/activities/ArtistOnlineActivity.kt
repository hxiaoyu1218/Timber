package com.naman14.amber.activities

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.support.annotation.StyleRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.FrameLayout
import com.afollestad.appthemeengine.Config
import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer
import com.afollestad.appthemeengine.customizers.ATEStatusBarCustomizer
import com.afollestad.appthemeengine.customizers.ATEToolbarCustomizer
import com.google.gson.Gson
import com.naman14.amber.R
import com.naman14.amber.adapters.OnlineSongListAdapter
import com.naman14.amber.models.Playlist
import com.naman14.amber.services.ServiceClient
import com.naman14.amber.services.SongListModel
import com.naman14.amber.utils.*
import com.naman14.amber.widgets.BaseRecyclerView
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import kotlinx.android.synthetic.main.activity_artist_online.*
import retrofit.RetrofitError
import retrofit.client.Response


/**
 *  Created by huangxiaoyu on 2019/4/29
 *
 */

class ArtistOnlineActivity : BaseActivity(), ATEActivityThemeCustomizer, ATEToolbarCustomizer,
    ATEStatusBarCustomizer {

    private lateinit var recyclerView: BaseRecyclerView
    private lateinit var adapter: OnlineSongListAdapter
    private val playlist = Playlist()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



//        if (PreferencesUtility.getInstance(this).theme == "dark") {
//            setTheme(R.style.AppThemeDark)
//        }
//        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false)) {
//            window.decorView.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        } else {
//            if (AmberUtils.isMarshmallow()) {
//                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
//            } else {
//                window.decorView.systemUiVisibility =
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            }
//        }
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_artist_online)

        val id = intent.getStringExtra(Constants.ARTIST_ID)
        val name = intent.getStringExtra(Constants.ARTIST_NAME)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)




        artist_name.text = name
//        artist_name.setTextColor(Config.textColorPrimary(this, Helpers.getATEKey(this)))
//        artist_count.setTextColor(Config.textColorPrimary(this, Helpers.getATEKey(this)))
        artist_name.setTextColor(Color.WHITE)
        artist_count.setTextColor(Color.WHITE)
//        ImageLoader.getInstance().displayImage(
//            ServiceClient.RES_SERVICE_URL + "/artist_pic?artist_id=" + id,
//            artist_avatar, DisplayImageOptions.Builder().cacheInMemory(true)
//                .showImageOnLoading(R.drawable.holder)
//                .resetViewBeforeLoading(true).build()
//        )
        ImageLoader.getInstance()
            .displayImage(ServiceClient.RES_SERVICE_URL + "/artist_pic?artist_id=" + id, artist_avatar,
                          DisplayImageOptions.Builder().cacheInMemory(true)
                              .showImageOnFail(R.drawable.holder)
                              .build(), object : SimpleImageLoadingListener() {

                    override fun onLoadingComplete(
                        imageUri: String?,
                        view: View?,
                        loadedImage: Bitmap?
                    ) {
                        doAlbumArtStuff(loadedImage!!)
                    }

                    override fun onLoadingFailed(
                        imageUri: String?,
                        view: View?,
                        failReason: FailReason?
                    ) {
                        val failedBitmap = ImageLoader.getInstance()
                            .loadImageSync("drawable://" + R.drawable.holder)
                        doAlbumArtStuff(failedBitmap)
                    }

                })


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = name


        recyclerView = findViewById(R.id.recyclerview)
        val layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        recyclerView.layoutParams = layoutParams
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = OnlineSongListAdapter(this@ArtistOnlineActivity)
        recyclerView.adapter = adapter

        ServiceClient.getArtitContent(id, object : retrofit.Callback<String> {
            override fun success(t: String?, response: Response?) {
                val data = Gson().fromJson<SongListModel>(t, SongListModel::class.java)
                adapter.bindData(data.songList)
                artist_count.text = String.format(getString(R.string.count_songs), data.count)
            }

            override fun failure(error: RetrofitError?) {

            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuInflater.inflate(R.menu.menu_list_online, menu)
        //ATE.applyMenu(this, ateKey, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            if (it.itemId == android.R.id.home) {
                super.onBackPressed()
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


    fun doAlbumArtStuff(loadedImage: Bitmap) {
        val blurredAlbumArt = BlurredAlbumArt()
        blurredAlbumArt.execute(loadedImage)
    }

    private inner class BlurredAlbumArt : AsyncTask<Bitmap, Void, Drawable>() {

        override fun doInBackground(vararg loadedImage: Bitmap): Drawable? {
            var drawable: Drawable? = null
            try {
                drawable =
                    ImageUtils.createBlurredImageFromBitmap(loadedImage[0], this@ArtistOnlineActivity, 12)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return drawable
        }

        override fun onPostExecute(result: Drawable?) {
            if (result != null) {
                if (test.getBackground() != null) {
                    val td = TransitionDrawable(arrayOf(test.getBackground(), result))
                    test.setBackground(td)
                    td.startTransition(200)

                } else {
                    test.setBackground(result)
                }
            }
        }

        override fun onPreExecute() {}
    }


    @StyleRes
    override fun getActivityTheme(): Int {
        return R.style.PlayerThemeFullScreen
    }

    override fun getLightToolbarMode(): Int {
        return Config.LIGHT_TOOLBAR_AUTO
    }

    override fun getLightStatusBarMode(): Int {
        return Config.LIGHT_STATUS_BAR_OFF
    }

    override fun getToolbarColor(): Int {
        return Color.TRANSPARENT
    }

    override fun getStatusBarColor(): Int {
        return Color.TRANSPARENT
    }


}