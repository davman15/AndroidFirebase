package com.example.animezone

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_menu_principal.*
import kotlinx.android.synthetic.main.card_post.view.*

class MenuPrincipalActivity : AppCompatActivity() {
    private val autentificacion = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)
        //Sustituyo el valor del TextView, respecto al usuario que esta conectado y su imagen de Perfil
        cargarDatosPersonales()


        //Para ir a la lista de Todas las publicaciones
        listaPublicaciones.setOnClickListener {
            val intent = Intent(this, ListaPublicacionesActivity::class.java)
            startActivity(intent)
        }
        cerrarSesion()
    }

    private fun cargarDatosPersonales() {
        usuarioActual.text = autentificacion.currentUser.displayName
        Glide.with(this)
            .load(autentificacion.currentUser.photoUrl)
            .fitCenter()
            .into(imagenPerfilMenu)
    }

    private fun cerrarSesion() {
        cerrarSesion.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Cierre de Sesión")
                setMessage("¿Estás seguro que quieres cerrar sesión?")
                setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Si</font>")) { dialogInterface: DialogInterface, i: Int ->
                    autentificacion.signOut()
                    finish()
                }
                setNegativeButton(Html.fromHtml("<font color='#FFFFFF'>No</font>"), null)
            }.show()
        }
    }

}