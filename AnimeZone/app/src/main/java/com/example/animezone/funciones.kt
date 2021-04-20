package com.example.animezone

import android.content.Context
import android.text.Html
import androidx.appcompat.app.AlertDialog

object funciones {
    fun showError(context: Context, message:String){
        //Si da error el correo pues saltará un dialogo al usuario
        AlertDialog.Builder(context).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>"),null)
        }.show()
    }
}