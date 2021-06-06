package com.example.animezone.Perfil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.animezone.Chat.ListaChatsActivity
import com.example.animezone.Clase.Seguidor
import com.example.animezone.Notificaciones.Notificacion
import com.example.animezone.R
import com.example.animezone.Top.TopAjenoActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_perfil_ajeno.*
import java.text.SimpleDateFormat
import java.util.*

class PerfilAjenoActivity : AppCompatActivity() {
    private val autentificacion = Firebase.auth
    private val baseDatos = Firebase.firestore
    private val coleccionUsuarios = baseDatos.collection("Usuarios")
    private var usuarioChat = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_ajeno)
        perfilajeno_circulo1.visibility= View.VISIBLE
        perfilajeno_circulo2.visibility= View.VISIBLE
        /*Aqui recibe informacion desde la lista de Publicaciones*/
        var usuarioNombre = intent.getStringExtra("UsuarioInfo").toString()
        //Si no recibe informacion por la lista de Publicaciones
        if (usuarioNombre == "null") {
            //Recibirá informacion desde el chat
            intent.getStringExtra("UsuarioChat")?.let {
                usuarioChat = it
            }
            coleccionUsuarios.document(usuarioChat).get()
                .addOnSuccessListener {
                    //Rellenar campos
                    nombre_imagenPerfilAjeno.setText(it.getString("usuarioId").toString())
                    nombrePerfilAjeno_tx.setText(it.getString("nombreUsuario").toString())
                    apellidosPerfilAjeno_tx.setText(it.getString("apellidos").toString())
                    correoPerfilAjeno_tx.setText(it.getString("correo").toString())
                    nicknamePerfilAjeno_tx.setText(it.getString("usuarioId").toString())
                    if(nombre_imagenPerfilAjeno.text.toString()=="null"){
                        nombre_imagenPerfilAjeno.setText("No Disponible")
                        seguir_btn.isClickable=false
                        chatear_perfilAjeno_btn.isClickable=false
                        ver_suTop_btn.isClickable=false
                    }

                    if(nombrePerfilAjeno_tx.text.toString()=="null")
                       nombrePerfilAjeno_tx.setText("")
                    if(correoPerfilAjeno_tx.text.toString()=="null")
                        correoPerfilAjeno_tx.setText("")
                    if(nicknamePerfilAjeno_tx.text.toString()=="null")
                        nicknamePerfilAjeno_tx.setText("")

                    descripcionPerfilAjeno_tx.setText(it.getString("descripcion").toString())
                    if (apellidosPerfilAjeno_tx.text.toString() == "null")
                        apellidosPerfilAjeno_tx.setText("")

                    if (descripcionPerfilAjeno_tx.text.toString() == "null")
                        descripcionPerfilAjeno_tx.setText("")

                    var urlImagen = it.getString("imagen").toString()
                    if(urlImagen=="null"){
                        urlImagen="https://firebasestorage.googleapis.com/v0/b/animezone-82466.appspot.com/o/ImagenPerfilPorDefecto%2Fsinperfil.png?alt=media&token=79062551-4c24-45d7-9243-21030e6755b9"
                        Glide.with(this)
                            .load(urlImagen)
                            .fitCenter()
                            .into(imagenPerfilAjeno)
                    }
                    else{
                        Glide.with(this)
                            .load(urlImagen)
                            .fitCenter()
                            .into(imagenPerfilAjeno)
                    }
                    perfilajeno_circulo1.visibility= View.INVISIBLE
                    perfilajeno_circulo2.visibility= View.INVISIBLE
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
            intent.putExtra("UsuarioChat", nombre_imagenPerfilAjeno.text.toString())
            startActivity(intent)
        }

        //Boton para poder ver su Top
        ver_suTop_btn.setOnClickListener {
            val intent = Intent(this, TopAjenoActivity::class.java)
            intent.putExtra("Usuario_Top", nombre_imagenPerfilAjeno.text.toString())
            startActivity(intent)
        }


        //Si le quiere seguir se añade a una subcoleccion llamada Seguidores donde se le añade al seguidor con su nombre y su foto.
        //Si le da de nuevo pues se eliminara de esa subcolleccion ese seguidor
        seguir_btn.setOnClickListener {
            if (seguir_btn.text.toString() == "Seguir") {
                seguir_btn.setText("Seguido")
                val seguidor = Seguidor(autentificacion.currentUser.displayName)
                coleccionUsuarios.document(nombre_imagenPerfilAjeno.text.toString())
                    .collection("Seguidores").document(seguidor.usuarioId.toString()).set(seguidor)
                    .addOnSuccessListener {
                        enviarNotificacion()
                    }
            } else {
                seguir_btn.setText("Seguir")
                baseDatos.collection("Usuarios").document(nombre_imagenPerfilAjeno.text.toString())
                    .collection("Seguidores").document(autentificacion.currentUser.displayName)
                    .delete().addOnSuccessListener {
                        eliminarDeSeguidos()
                    }
            }
        }
    }

    private fun eliminarDeSeguidos() {
        baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName)
            .collection("Seguidos")
            .document(nombre_imagenPerfilAjeno.text.toString()).delete()
    }

    private fun enviarNotificacion() {
        var fechaId = ""
        val referenciaUsuarios = baseDatos.collection("Usuarios")
        val fechaFormateada = SimpleDateFormat("dd-M-yyyy hh:mm:ss")
        val notificacion = Notificacion( usuarioId = autentificacion.currentUser.displayName,
            mensaje = " te ha empezado a seguir.",
            fecha = Date(), id = autentificacion.currentUser.displayName+"-"+fechaFormateada.format(Date()))
        fechaId = fechaFormateada.format(notificacion.fecha)

        referenciaUsuarios.document(nombre_imagenPerfilAjeno.text.toString()).collection("Notificaciones")
            .document(autentificacion.currentUser.displayName + "-" + fechaId)
            .set(notificacion).addOnSuccessListener {
                referenciaUsuarios.document(nombre_imagenPerfilAjeno.text.toString()).collection("Notificaciones No Leidas")
                    .document(autentificacion.currentUser.displayName + "-" + fechaId).set(notificacion).addOnSuccessListener {
                        anadirSeguidos()
                    }
            }
    }

    private fun anadirSeguidos() {
        val seguidor = Seguidor(nombre_imagenPerfilAjeno.text.toString())
        baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName)
            .collection("Seguidos").document(nombre_imagenPerfilAjeno.text.toString()).set(seguidor)
    }

    private fun seguir_dejar_Seguir() {
        baseDatos.collection("Usuarios").document(usuarioChat)
            .collection("Seguidores").document(autentificacion.currentUser.displayName)
            .get()
            .addOnSuccessListener { esSeguidor ->
                if (esSeguidor.exists())
                    seguir_btn.setText("Seguido")
                else
                    seguir_btn.setText("Seguir")
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
                if (apellidosPerfilAjeno_tx.text.toString() == "null")
                    apellidosPerfilAjeno_tx.setText("")

                if (descripcionPerfilAjeno_tx.text.toString() == "null")
                    descripcionPerfilAjeno_tx.setText("")

                var urlImagen = it.getString("imagen").toString()
                Glide.with(this)
                    .load(urlImagen)
                    .fitCenter()
                    .into(imagenPerfilAjeno)
                perfilajeno_circulo1.visibility= View.INVISIBLE
                perfilajeno_circulo2.visibility= View.INVISIBLE
            }
    }
}