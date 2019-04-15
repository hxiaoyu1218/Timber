package com.naman14.amber.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.naman14.amber.R

/**
 *   Created by huangxiaoyu
 *   Time 2019/4/15
 **/

class AutoShutdownDialog : DialogFragment(), View.OnClickListener {

    companion object {
        fun showAutoShutdownDialog(activity: AppCompatActivity, c: (x: Int) -> Boolean) {
            val fm = activity.supportFragmentManager
            val ft = fm.beginTransaction()
            val prev = fm.findFragmentByTag("dialog_st")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)

            AutoShutdownDialog().setOnClick(c).show(ft, "dialog_st")
        }
    }

    var click: (x: Int) -> Boolean? = { false }

    fun setOnClick(c: (x: Int) -> Boolean): AutoShutdownDialog {
        click = c
        return this
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val layoutInflater = activity!!.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rootView = layoutInflater.inflate(R.layout.layout_shutdown_dialog, null) as LinearLayout

        val disable = rootView.findViewById<TextView>(R.id.shutdown_disable)
        val five = rootView.findViewById<TextView>(R.id.shutdown_five)
        val fifteen = rootView.findViewById<TextView>(R.id.shutdown_quarter)
        val half = rootView.findViewById<TextView>(R.id.shutdown_half)
        val hour = rootView.findViewById<TextView>(R.id.shutdown_hour)


        disable.setOnClickListener(this)
        five.setOnClickListener(this)
        fifteen.setOnClickListener(this)
        half.setOnClickListener(this)
        hour.setOnClickListener(this)


        return AlertDialog.Builder(activity!!)
                .setView(rootView)
                .create()
    }

    override fun onClick(v: View?) {
        v?.let {
            click.invoke(it.id)
            dismiss()
        }
    }
}