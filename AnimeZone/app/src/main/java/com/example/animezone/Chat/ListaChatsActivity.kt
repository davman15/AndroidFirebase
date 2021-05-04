package com.example.animezone.Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Html
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animezone.ProgressBar.CargandoDialog
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_lista_chats.*

class ListaChatsActivity : AppCompatActivity() {
    private var autentificacion = Firebase.auth
    private var usuarioBusqueda = autentificacion.currentUser.displayName
    private var baseDatos = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_chats)

        if (usuarioBusqueda.isNotEmpty()) {
            iniciarBusqueda()
        }
    }

    private fun iniciarBusqueda() {
        buscarChat.setOnClickListener {
            if(nuevoChat_tx.text.toString()==""){
                negarChat()
            }
            else {
                nuevoChat()
            }
        }

        //Enseñar ProgressBar
        val cargando = CargandoDialog(this)
        cargando.empezarCarga()
        val handler = Handler()
        handler.postDelayed({ cargando.cancelable() }, 2600)

        listaChatsRecyclerView.layoutManager = LinearLayoutManager(this)
        listaChatsRecyclerView.adapter = ChatAdapter { chat ->
            chatSeleccionado(chat)
        }

        val usuarioReferencia = baseDatos.collection("Usuarios").document(usuarioBusqueda)
        usuarioReferencia.collection("chats").get().addOnSuccessListener { chats ->
            val listaChats = chats.toObjects(Chat::class.java)

            (listaChatsRecyclerView.adapter as ChatAdapter).setData(listaChats)

        }
        //Va a estar a la espera cuando se creen nuevos chats
        usuarioReferencia.collection("chats")
            .addSnapshotListener { chats, error ->
                if (error == null) {
                    chats?.let {
                        val listaChats = chats.toObjects(Chat::class.java)
                        (listaChatsRecyclerView.adapter as ChatAdapter).setData(listaChats)
                    }
                }
            }
    }

    //Para seleccionar un chat que ya habia sido creado
    private fun chatSeleccionado(chat: Chat) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatId", chat.id)
        intent.putExtra("usuario", usuarioBusqueda)
        if (autentificacion.currentUser.displayName == chat.usuarios.first()) {
            intent.putExtra("otroUsuario", chat.nombre)
        } else if (autentificacion.currentUser.displayName == chat.usuarios.last()) {
            intent.putExtra("otroUsuario", chat.usuarios.first())
        }

        startActivity(intent)
    }

    //Para crear un nuevo chat
    private fun nuevoChat() {
        val chatID = autentificacion.currentUser.displayName + "-" + nuevoChat_tx.text.toString()
        val otroUsuario = nuevoChat_tx.text.toString()
        val intent = Intent(this, ChatActivity::class.java)

        baseDatos.collection("Usuarios").document(otroUsuario).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    crearChat(otroUsuario, chatID, intent)
                } else {
                    negarChat()
                }
            }
    }

    private fun crearChat(otroUsuario: String, chatID: String, intent: Intent) {
        //Creo una lista donde pongo el usuario
        val usuarios = listOf(usuarioBusqueda, otroUsuario)

        //Y lo pongo en el chat
        val chat = Chat(
            id = chatID,
            nombre = "$otroUsuario",
            usuarios = usuarios
        )

        baseDatos.collection("chats").document(chatID).set(chat)
        baseDatos.collection("Usuarios").document(usuarioBusqueda).collection("chats")
            .document(chatID).set(chat)
        baseDatos.collection("Usuarios").document(otroUsuario).collection("chats")
            .document(chatID).set(chat)
        //Estos datos del chat los paso de esta forma al activity que yo quiera
        intent.putExtra("chatId", chatID)
        intent.putExtra("usuario", usuarioBusqueda)
        intent.putExtra("otroUsuario", otroUsuario)
        startActivity(intent)
    }

    /*private fun comprobarChatExistente(chatID: String, otroUsuario: String, intent: Intent) {
        baseDatos.collection("Usuarios")
            .document(autentificacion.currentUser.displayName + "-" + nuevoChat_tx.text.toString())
            .collection("chats").document(chatID).get()
            .addOnSuccessListener { chat1 ->
                if (!chat1.exists())
                    crearChat(otroUsuario, chatID, intent)
                else if(chat1.exists()) {
                    AlertDialog.Builder(this).apply {
                        setTitle("Ola 1")
                        setMessage("El usuario introducido no existe")
                        setPositiveButton(
                            Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>"),
                            null
                        )
                    }.show()
                    baseDatos.collection("Usuarios")
                        .document(nuevoChat_tx.text.toString() + "-" + autentificacion.currentUser.displayName)
                        .collection("chats").document(chatID).get()
                        .addOnSuccessListener { chat2 ->
                            if (!chat2.exists()) {
                                crearChat(otroUsuario, chatID, intent)
                            }
                            else if(chat2.exists()){
                                AlertDialog.Builder(this).apply {
                                    setTitle("Ola 2")
                                    setMessage("El usuario introducido no existe")
                                    setPositiveButton(
                                        Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>"),
                                        null
                                    )
                                }.show()
                            }
                        }
                }
            }
    }*/

    private fun negarChat() {
        AlertDialog.Builder(this).apply {
            setTitle("Usuario Inválido")
            setMessage("El usuario introducido no existe")
            setPositiveButton(
                Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>"),
                null
            )
        }.show()
    }
}