package com.example.animezone.Chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.animezone.R
import kotlinx.android.synthetic.main.chat_item.view.*

class ChatAdapter(val chatClick: (Chat) -> Unit): RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    var chats: List<Chat> = emptyList()

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
        holder.itemView.chatNombre.text = chats[position].nombre
        holder.itemView.usuariosTextView.text = chats[position].usuarios.toString()
        /*for (i in 0..chats.size){
                if(holder.itemView.chatNombre.text.toString()==chats[i].nombre){
                    Log.i("TAG",chats[i].nombre)
                }
            }*/

        holder.itemView.setOnClickListener {
            chatClick(chats[position])
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }



    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}