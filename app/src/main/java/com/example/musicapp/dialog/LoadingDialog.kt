package com.example.musicapp.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import com.example.musicapp.R


class LoadingDialog {
    lateinit var activity: Activity
    lateinit var text : TextView
    lateinit var isdialog : AlertDialog

    constructor(activity: Activity) {
        this.activity= activity
    }


    fun ShowDialog(title:String){
        val infalter = activity.layoutInflater
        val dialogview = infalter.inflate(R.layout.dialog_loading,null)

        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogview)
        builder.setCancelable(false)
        isdialog = builder.create()
        val o = ColorDrawable(Color.TRANSPARENT)
        isdialog.window?.setBackgroundDrawable(o)
        isdialog.show()

    }

    fun HideDialog(){
        isdialog.dismiss()
    }

}