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

class ChatAdapter(val chatClick: (Chat) -> Unit) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    var chats: List<Chat> = emptyList()
    private var autentificacion = Firebase.auth
    private var baseDatos = Firebase.firestore

    fun setData(list: List<Chat>) {
        chats = list
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
        if (conversaciones.usuarios.first().toString() == usuarioActual) {
            holder.itemView.chatNombre.text = conversaciones.usuarios.last()
            holder.itemView.usuariosTextView.text = chats[position].usuarios.toString()
            baseDatos.collection("Usuarios").document(conversaciones.usuarios.last())
                .get()
                .addOnSuccessListener {
                    var urlImagen = it.getString("imagen").toString()
                    Glide.with(holder.itemView.context)
                        .load(urlImagen)
                        .fitCenter()
                        .into(holder.itemView.imagenPerfilListaChat)
                }
        } else if (conversaciones.usuarios.last().toString() == usuarioActual) {
            holder.itemView.chatNombre.text = conversaciones.usuarios.first()
            holder.itemView.usuariosTextView.text = conversaciones.usuarios.toString()
            baseDatos.collection("Usuarios").document(conversaciones.usuarios.first())
                .get()
                .addOnSuccessListener {
                    var urlImagen = it.getString("imagen").toString()
                    Glide.with(holder.itemView.context)
                        .load(urlImagen)
                        .fitCenter()
                        .into(holder.itemView.imagenPerfilListaChat)
                }
        }
        holder.itemView.setOnClickListener {
            chatClick(conversaciones)
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }


    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}