package com.example.animezone.Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_lista_chats.*
import java.util.*

class ListaChatsActivity : AppCompatActivity() {
    private var autentificacion = Firebase.auth
    private var usuarioBusqueda = autentificacion.currentUser.displayName
    private var baseDatos = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_chats)
        listaChats_circulo1.visibility= View.VISIBLE
        listaChats_circulo2.visibility= View.VISIBLE
        //Aqui recibo la información si el usuario quiere chatear con otro usuario
        var usuarioChat=intent.getStringExtra("UsuarioChat").toString()

        //De tal forma si quiere hablar con alguien se rellena el buscador con el contacto que queria y si no pues el texto sera null
        if(usuarioChat=="null")
            nuevoChat_tx.setText("")
        else
            nuevoChat_tx.setText(usuarioChat)

        if (usuarioBusqueda.isNotEmpty())
            iniciarBusqueda()

    }

    private fun iniciarBusqueda() {
        buscarChat.setOnClickListener {
            if (nuevoChat_tx.text.toString() == "")
                negarChat()
            else
                nuevoChat()
        }
        listaChatsRecyclerView.layoutManager = LinearLayoutManager(this)
        listaChatsRecyclerView.adapter = ChatAdapter { chat ->
            chatSeleccionado(chat)
        }

        val usuarioReferencia = baseDatos.collection("Usuarios").document(usuarioBusqueda)
        usuarioReferencia.collection("chats").orderBy("fechaChat", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                val listaChats = value!!.toObjects(Chat::class.java)
                (listaChatsRecyclerView.adapter as ChatAdapter).setData(listaChats)
                if(listaChats.size<=0)
                    notieneschats_tv.visibility=View.VISIBLE
                else
                    notieneschats_tv.visibility=View.INVISIBLE

                listaChats_circulo1.visibility= View.INVISIBLE
                listaChats_circulo2.visibility= View.INVISIBLE
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
        //Cada vez que doy a un chat existente se actualizara la fecha y ese chat se pondrá en primer lugar ya que lo ordeno por la fecha
        baseDatos.collection("Usuarios").document(usuarioBusqueda).collection("chats")
            .document(chat.id).update("fechaChat", Date())
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
                    baseDatos.collection("Usuarios")
                        .document(usuarioBusqueda)
                        .collection("chats")
                        .document(nuevoChat_tx.text.toString() + "-" + autentificacion.currentUser.displayName)
                        .get()
                        .addOnSuccessListener { chat1 ->
                            if (!chat1.exists())
                                crearChat(otroUsuario, chatID, intent)
                            else if (chat1.exists()) {
                                val chatID = nuevoChat_tx.text.toString() + "-" + autentificacion.currentUser.displayName
                                crearChat(otroUsuario, chatID, intent)
                            }
                        }
                }
                else {
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
            usuarios = usuarios,
            fechaChat = Date()
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