package com.example.animezone.Perfil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.activity_perfil_ajeno.*

class PerfilAjenoActivity : AppCompatActivity() {
    private val autentificacion = Firebase.auth
    private val baseDatos = Firebase.firestore
    private val coleccionUsuarios = baseDatos.collection("Usuarios")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_ajeno)
        /*Segun que usuario es elegido se coge el valor de la etiqueta que contiene el id del usuario asi se puede pasar facil e identificar todos sus datos
        y se pasa aqui a esta activity*/
        val intentRecibe: Intent =intent
        var usuarioNombre=intent.getStringExtra("UsuarioInfo")
        nombre_imagenPerfilAjeno.setText(usuarioNombre)

        coleccionUsuarios.document(usuarioNombre.toString()).get()
            .addOnSuccessListener {
                //Rellenar campos
                nombrePerfilAjeno_tx.setText(it.getString("nombreUsuario").toString())
                apellidosPerfilAjeno_tx.setText(it.getString("apellidos").toString())
                correoPerfilAjeno_tx.setText(it.getString("correo").toString())
                nicknamePerfilAjeno_tx.setText(it.getString("usuarioId").toString())
                if(apellidosPerfilAjeno_tx.text.toString()=="null"){
                    apellidosPerfilAjeno_tx.setText("")
                }

                var urlImagen = it.getString("imagen").toString()
                Glide.with(this)
                    .load(urlImagen)
                    .fitCenter()
                    .into(imagenPerfilAjeno)
            }
    }
}