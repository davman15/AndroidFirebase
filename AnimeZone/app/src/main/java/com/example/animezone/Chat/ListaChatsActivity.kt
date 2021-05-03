package com.example.animezone.Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_lista_chats.*
import java.util.*

class ListaChatsActivity : AppCompatActivity() {
    private var autentificacion=Firebase.auth
    private var usuarioBusqueda = autentificacion.currentUser.displayName
    private var baseDatos=Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_chats)

        if (usuarioBusqueda.isNotEmpty()) {
            iniciarBusqueda()
        }
    }

    private fun iniciarBusqueda() {
        buscarChat.setOnClickListener {
            nuevoChat()
        }

        listaChatsRecyclerView.layoutManager = LinearLayoutManager(this)
        listaChatsRecyclerView.adapter = ChatAdapter { chat -> chatSeleccionado(chat)}
        val usuarioReferencia=baseDatos.collection("Usuarios").document(usuarioBusqueda)
        usuarioReferencia.collection("chats").get().addOnSuccessListener { chats->
            val listaChats=chats.toObjects(Chat::class.java)

            /*for (i in 0..listaChats.size){
                listaChats[i].nombre
            }*/

            (listaChatsRecyclerView.adapter as ChatAdapter).setData(listaChats)
        }
        //Va a estar a la espera cuando se creen nuevos chats
        usuarioReferencia.collection("chats")
            .addSnapshotListener { chats, error ->
                if(error==null){
                    chats?.let {
                        val listaChats=chats.toObjects(Chat::class.java)
                        (listaChatsRecyclerView.adapter as ChatAdapter).setData(listaChats)
                    }
                }
            }
    }

    //Para seleccionar un chat que ya habia sido creado... creo
    private fun chatSeleccionado(chat:Chat) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatId", chat.id)
        intent.putExtra("usuario", usuarioBusqueda)
        startActivity(intent)
    }

    //Para crear un nuevo chat
    private fun nuevoChat() {
        val chatID = UUID.randomUUID().toString()
        val otroUsuario=nuevoChat_tx.text.toString()
        val intent = Intent(this, ChatActivity::class.java)

        //Creo una lista donde pongo el usuario
        val usuarios= listOf(usuarioBusqueda,otroUsuario)
        //Y lo pongo en el chat
        val chat=Chat(
            id=chatID,
            nombre="$otroUsuario",
            usuarios = usuarios
        )

        baseDatos.collection("chats").document(chatID).set(chat)
        baseDatos.collection("Usuarios").document(usuarioBusqueda).collection("chats").document(chatID).set(chat)
        baseDatos.collection("Usuarios").document(otroUsuario).collection("chats").document(chatID).set(chat)

        intent.putExtra("chatId", chatID)
        intent.putExtra("usuario", usuarioBusqueda)
        startActivity(intent)
    }
}