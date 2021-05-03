package com.example.animezone.ProgressBar

import android.app.Activity
import android.app.AlertDialog
import com.example.animezone.R

class CargandoDialog(val miActivity: Activity) {
    private lateinit var dialog:AlertDialog
    fun empezarCarga(){
        val miVista=miActivity.layoutInflater
        val dialogView=miVista.inflate(R.layout.loading_item,null)
        val dialogo=AlertDialog.Builder(miActivity)
        dialogo.setView(dialogView)
        dialogo.setCancelable(false)
        dialog=dialogo.create()
        dialog.show()
    }
    fun cancelable(){
        dialog.dismiss()
    }
}