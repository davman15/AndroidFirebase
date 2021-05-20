package com.example.animezone.Perfil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.animezone.Chat.ChatActivity
import com.example.animezone.Chat.ListaChatsActivity
import com.example.animezone.Clase.Seguidor
import com.example.animezone.Clase.Usuario
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
    private var usuarioChat = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_ajeno)
        /*Aqui recibe informacion desde la lista de Publicaciones*/
        var usuarioNombre = intent.getStringExtra("UsuarioInfo").toString()
        //Si no recibe informacion por la lista de Publicaciones
        if (usuarioNombre == "null") {
            //Recibirá informacion desde el chat
            intent.getStringExtra("UsuarioChat")?.let {
                usuarioChat = it
            }
            nombre_imagenPerfilAjeno.setText(usuarioChat)
            coleccionUsuarios.document(usuarioChat).get()
                .addOnSuccessListener {
                    //Rellenar campos
                    nombrePerfilAjeno_tx.setText(it.getString("nombreUsuario").toString())
                    apellidosPerfilAjeno_tx.setText(it.getString("apellidos").toString())
                    correoPerfilAjeno_tx.setText(it.getString("correo").toString())
                    nicknamePerfilAjeno_tx.setText(it.getString("usuarioId").toString())
                    descripcionPerfilAjeno_tx.setText(it.getString("descripcion").toString())
                    if (apellidosPerfilAjeno_tx.text.toString() == "null") {
                        apellidosPerfilAjeno_tx.setText("")
                    }

                    var urlImagen = it.getString("imagen").toString()
                    Glide.with(this)
                        .load(urlImagen)
                        .fitCenter()
                        .into(imagenPerfilAjeno)
                }
        } else {
            //Si recibe información pues será rellenado de la lista de publicaciones
            mostrarDePublicaciones(usuarioNombre)
        }

        //Controlo aqui si le sigue a este usuario
        seguir_dejar_Seguir()

        //Boton para chatear con el contacto
        chatear_perfilAjeno_btn.setOnClickListener {
            val intent = Intent(this, ListaChatsActivity::class.java)
            intent.putExtra("UsuarioChat", nicknamePerfilAjeno_tx.text.toString())
            startActivity(intent)
        }

        //Si le quiere seguir se añade a una subcoleccion llamada Seguidores donde se le añade al seguidor con su nombre y su foto.
        //Si le da de nuevo pues se eliminara de esa subcolleccion ese seguidor
        seguir_btn.setOnClickListener {
            if (seguir_btn.text.toString() == "Seguir") {
                seguir_btn.setText("Seguido")
                val seguidor = Seguidor(
                    autentificacion.currentUser.displayName,
                    autentificacion.currentUser.photoUrl.toString()
                )
                coleccionUsuarios.document(nicknamePerfilAjeno_tx.text.toString())
                    .collection("Seguidores").document(seguidor.usuarioId.toString()).set(seguidor)
                    .addOnSuccessListener {
                    }
            } else {
                seguir_btn.setText("Seguir")
                baseDatos.collection("Usuarios").document(nicknamePerfilAjeno_tx.text.toString())
                    .collection("Seguidores").document(autentificacion.currentUser.displayName)
                    .delete().addOnSuccessListener {

                    }
            }
        }
    }

    private fun seguir_dejar_Seguir() {
        baseDatos.collection("Usuarios").document(usuarioChat)
            .collection("Seguidores").document(autentificacion.currentUser.displayName)
            .get()
            .addOnSuccessListener { esSeguidor ->
                if (esSeguidor.exists()) {
                    seguir_btn.setText("Seguido")
                } else {
                    seguir_btn.setText("Seguir")
                }
            }
    }


    private fun mostrarDePublicaciones(usuarioNombre: String?) {
        nombre_imagenPerfilAjeno.setText(usuarioNombre)
        coleccionUsuarios.document(usuarioNombre.toString()).get()
            .addOnSuccessListener {
                //Rellenar campos
                nombrePerfilAjeno_tx.setText(it.getString("nombreUsuario").toString())
                apellidosPerfilAjeno_tx.setText(it.getString("apellidos").toString())
                correoPerfilAjeno_tx.setText(it.getString("correo").toString())
                nicknamePerfilAjeno_tx.setText(it.getString("usuarioId").toString())
                descripcionPerfilAjeno_tx.setText(it.getString("descripcion").toString())
                if (apellidosPerfilAjeno_tx.text.toString() == "null") {
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