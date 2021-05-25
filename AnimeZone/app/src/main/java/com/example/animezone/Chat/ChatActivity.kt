package com.example.animezone.Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.animezone.Clase.Usuario
import com.example.animezone.Notificaciones.Notificacion
import com.example.animezone.Perfil.PerfilActivity
import com.example.animezone.Perfil.PerfilAjenoActivity
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {
    private var chat_id = ""
    private var usuario = ""
    private var baseDatos = Firebase.firestore
    private var otroUsuario = ""
    private var autentificacion = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        intent.getStringExtra("chatId")?.let {
            chat_id = it
        }
        intent.getStringExtra("usuario")?.let {
            usuario = it
        }
        intent.getStringExtra("otroUsuario")?.let {
            otroUsuario = it
        }

        if (chat_id.isNotEmpty() && usuario.isNotEmpty()) {
            iniciarVista()
        }
    }

    private fun iniciarVista() {
        mensajesRecylerView.layoutManager = LinearLayoutManager(this)
        mensajesRecylerView.adapter = MensajeAdapter(usuario)

        perfilChat_cv.setOnClickListener {
            var nombreUsuario = nombreOtroUsuarioChat.text.toString()
            val intent = Intent(this, PerfilAjenoActivity::class.java)
            val intentoPropio = Intent(this, PerfilActivity::class.java)
            intent.putExtra("UsuarioChat", nombreUsuario)
            if (nombreUsuario == autentificacion.currentUser.displayName)
                startActivity(intentoPropio)
            else
                startActivity(intent)
        }

        enviarMensaje_btn.setOnClickListener {
            enviarMensaje()
        }

        //Cargo los datos del otro Usuario
        baseDatos.collection("Usuarios").document(otroUsuario).get()
            .addOnSuccessListener {
                val usuarioObjeto = it.toObject<Usuario>()
                var urlImagen = usuarioObjeto?.imagen.toString()
                if (usuarioObjeto?.usuarioId.toString() == "null") {
                    perfilChat_cv.isClickable = false
                    urlImagen =
                        "https://firebasestorage.googleapis.com/v0/b/animezone-82466.appspot.com/o/ImagenPerfilPorDefecto%2Fsinperfil.png?alt=media&token=79062551-4c24-45d7-9243-21030e6755b9"
                    nombreOtroUsuarioChat.setText("No disponible")
                    Glide.with(this)
                        .load(urlImagen)
                        .fitCenter()
                        .into(imagenPerfilAjenoChat)
                } else {
                    nombreOtroUsuarioChat.setText(usuarioObjeto?.usuarioId.toString())
                    Glide.with(this)
                        .load(urlImagen)
                        .fitCenter()
                        .into(imagenPerfilAjenoChat)
                }
                cargarActualizarChat()
            }
    }

    private fun cargarActualizarChat() {
        val chatReferencia = baseDatos.collection("chats").document(chat_id)
        //Al recoger la lista de mensajes se ordenaran acorde a la fecha
        chatReferencia.collection("Mensajes").orderBy("fecha", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { mensajes ->
                val listaMensajes = mensajes.toObjects(Mensaje::class.java)
                (mensajesRecylerView.adapter as MensajeAdapter).setData(listaMensajes)
            }

        chatReferencia.collection("Mensajes").orderBy("fecha", Query.Direction.ASCENDING)
            .addSnapshotListener { mensajes, error ->
                if (error == null) {
                    mensajes?.let {
                        val listaMensajes = it.toObjects(Mensaje::class.java)
                        (mensajesRecylerView.adapter as MensajeAdapter).setData(
                            listaMensajes
                        )
                    }
                }
            }
    }

    private fun enviarMensaje() {
        if (mensajeCampoTexto.text.toString() != "") {
            //Creo el mensaje
            val mensaje = Mensaje(
                mensaje = mensajeCampoTexto.text.toString(),
                from = usuario
            )
            //Utilizo este update para que cuando alguien hable a otra persona se le ponga este chat en primer lugar de su lista de chats
            baseDatos.collection("Usuarios").document(nombreOtroUsuarioChat.text.toString())
                .collection("chats")
                .document(chat_id).update("fechaChat", Date()).addOnSuccessListener {
                }


            //Utilizo este get para que si el usuario manda su primer mensaje al otro usuario tambien en sus notificaciones aparezca que quiere hablar con el
            baseDatos.collection("chats").document(chat_id).collection("Mensajes").get().addOnSuccessListener {
                if(it.isEmpty){
                    enviarNotificacion(nombreOtroUsuarioChat.text.toString())
                    anadirMensaje(mensaje)
                }
                else
                    anadirMensaje(mensaje)
            }
            mensajeCampoTexto.setText("")
        }
    }

    private fun anadirMensaje(mensaje: Mensaje) {
        baseDatos.collection("chats").document(chat_id).collection("Mensajes").add(mensaje)
    }

    private fun enviarNotificacion(otroUsuario: String) {
        var fechaId = ""
        val referenciaUsuarios = baseDatos.collection("Usuarios")
        val fechaFormateada = SimpleDateFormat("dd-M-yyyy hh:mm:ss")
        val notificacion = Notificacion(
            usuarioId = autentificacion.currentUser.displayName,
            mensaje = " quiere empezar una nueva conversación contigo.",
            fecha = Date()
        )
        fechaId = fechaFormateada.format(notificacion.fecha)
        referenciaUsuarios.document(otroUsuario).collection("Notificaciones")
            .document(notificacion.usuarioId.toString() + "-" + fechaId)
            .set(notificacion).addOnSuccessListener {
                referenciaUsuarios.document(otroUsuario).collection("Notificaciones No Leidas")
                    .document(notificacion.usuarioId.toString() + "-" + fechaId).set(notificacion)
            }
    }
}