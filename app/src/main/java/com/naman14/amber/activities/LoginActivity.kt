package com.naman14.amber.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer
import com.naman14.amber.AmberApp
import com.naman14.amber.R
import com.naman14.amber.services.ServiceClient
import com.naman14.amber.utils.AmberUtils
import org.json.JSONObject
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response


/**
 *  Created by huangxiaoyu on 2019/5/2
 *
 */

class LoginActivity : BaseActivity(), ATEActivityThemeCustomizer {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AmberUtils.isMarshmallow()) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        } else {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_login)
        val uname = findViewById<EditText>(R.id.editText2)
        val upwd = findViewById<EditText>(R.id.editText)
        findViewById<Button>(R.id.button2).setOnClickListener {
            if (uname.text.isNullOrBlank() || upwd.text.isNullOrBlank()) {
                return@setOnClickListener
            }
            val map = HashMap<String, Any>()
            map.put("user_name", uname.text.trim().toString())
            map.put("pwd", upwd.text.trim().toString())
            ServiceClient.userLogin(ServiceClient.getJsonObject(map), object : Callback<String> {
                override fun success(t: String?, response: Response?) {
                    val o = JSONObject(t)
                    if (o.optString("result").equals("success")) {
                        val uid = o.optString("user_id")
                        AmberApp.getInstance().id = uid
                        val s = PreferenceManager.getDefaultSharedPreferences(this@LoginActivity).edit()
                        s.putBoolean("login", true)
                        s.putString("user_id", uid)
                        s.apply()
                        AmberApp.getInstance().loginMode = 1
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            R.string.lastfm_login_failture,
                            LENGTH_SHORT
                        ).show()
                    }
                }

                override fun failure(error: RetrofitError?) {
                    Toast.makeText(this@LoginActivity, R.string.lastfm_login_failture, LENGTH_SHORT)
                        .show()
                }
            })


        }

        findViewById<TextView>(R.id.guest).setOnClickListener {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra("guest", true)
            AmberApp.getInstance().loginMode = 2
            AmberApp.getInstance().id = AmberApp.getInstance().did
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.register).setOnClickListener {
            if (uname.text.isNullOrBlank() || upwd.text.isNullOrBlank()) {
                return@setOnClickListener
            }
            val map = HashMap<String, Any>()
            map.put("user_name", uname.text.trim().toString())
            map.put("pwd", upwd.text.trim().toString())
            ServiceClient.userRegister(ServiceClient.getJsonObject(map), object : Callback<String> {
                override fun success(t: String?, response: Response?) {
                    val o = JSONObject(t)
                    if (o.optString("result") == "success") {
                        val uid = o.optString("user_id")
                        AmberApp.getInstance().id = uid
                        val s =
                            PreferenceManager.getDefaultSharedPreferences(this@LoginActivity).edit()
                        s.putBoolean("login", true)
                        s.putString("user_id", uid)
                        s.apply()
                        AmberApp.getInstance().loginMode = 1
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            R.string.lastfm_login_failture,
                            LENGTH_SHORT
                        ).show()
                    }
                }

                override fun failure(error: RetrofitError?) {
                    Toast.makeText(this@LoginActivity, R.string.lastfm_login_failture, LENGTH_SHORT)
                        .show()
                }
            })

        }

    }

    override fun getActivityTheme(): Int {
        return R.style.AppThemeLight
    }
}
