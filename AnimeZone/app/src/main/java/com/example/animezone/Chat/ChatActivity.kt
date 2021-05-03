package com.example.animezone.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animezone.R
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    private var chat_id = ""
    private var usuario = ""
    private var baseDatos = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        intent.getStringExtra("chatId")?.let {
            chat_id = it
        }
        intent.getStringExtra("usuario")?.let {
            usuario = it
        }

        if (chat_id.isNotEmpty() && usuario.isNotEmpty()) {
            iniciarVista()
        }
    }

    private fun iniciarVista() {
        mensajesRecylerView.layoutManager = LinearLayoutManager(this)
        mensajesRecylerView.adapter = MensajeAdapter(usuario)
        enviarMensaje_btn.setOnClickListener {
            enviarMensaje()
        }

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
                        (mensajesRecylerView.adapter as MensajeAdapter).setData(listaMensajes)
                    }
                }
            }

    }

    private fun enviarMensaje() {
        if(mensajeCampoTexto.text.toString()!="") {
            //Creo el mensaje
            val mensaje = Mensaje(
                mensaje = mensajeCampoTexto.text.toString(),
                from = usuario
            )
            baseDatos.collection("chats").document(chat_id).collection("Mensajes").document()
                .set(mensaje)

            mensajeCampoTexto.setText("")
        }
    }

}