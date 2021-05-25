package com.example.animezone.Chat


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.chat_item.view.*
import java.text.SimpleDateFormat

class ChatAdapter(val chatClick: (Chat) -> Unit) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    var chats: List<Chat> = emptyList()
    private var autentificacion = Firebase.auth
    private var baseDatos = Firebase.firestore

    fun setData(lista: List<Chat>) {
        chats = lista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.chat_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        var usuarioActual = autentificacion.currentUser.displayName
        val conversaciones = chats[position]
        //Aqui se gestiona para que en el chat se vea las mismas conversaciones pero el perfil cambiado
        if (conversaciones.usuarios.first().toString() == usuarioActual) {
            holder.itemView.chatNombre.text = conversaciones.usuarios.last()
            holder.itemView.usuariosTextView.text = chats[position].usuarios.toString()
            baseDatos.collection("Usuarios").document(conversaciones.usuarios.last())
                .get()
                .addOnSuccessListener {
                    var urlImagen = it.getString("imagen").toString()
                    comprobarImagen(urlImagen, holder)
                }
        } else if (conversaciones.usuarios.last().toString() == usuarioActual) {
            holder.itemView.chatNombre.text = conversaciones.usuarios.first()
            holder.itemView.usuariosTextView.text = conversaciones.usuarios.toString()
            baseDatos.collection("Usuarios").document(conversaciones.usuarios.first())
                .get()
                .addOnSuccessListener {
                    var urlImagen = it.getString("imagen").toString()
                    comprobarImagen(urlImagen, holder)
                }
        }
        holder.itemView.setOnClickListener {
            chatClick(conversaciones)
        }
    }

    private fun comprobarImagen(
        urlImagen: String,
        holder: ChatViewHolder
    ) {
        if (urlImagen == "null") {
            Glide.with(holder.itemView.context)
                .load("https://firebasestorage.googleapis.com/v0/b/animezone-82466.appspot.com/o/ImagenPerfilPorDefecto%2Fsinperfil.png?alt=media&token=79062551-4c24-45d7-9243-21030e6755b9")
                .fitCenter()
                .into(holder.itemView.imagenPerfilListaChat)
        } else {
            Glide.with(holder.itemView.context)
                .load(urlImagen)
                .fitCenter()
                .into(holder.itemView.imagenPerfilListaChat)
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }


    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}