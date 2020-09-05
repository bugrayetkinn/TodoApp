package com.yetkin.todoapp

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */
class MyAlertDialog(private val context: Context) {

    private val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
    val alertDialog: AlertDialog = alertDialogBuilder.create()

    fun createDialog(view: View) {

        alertDialog.setView(view)
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}