package com.example.animezone.Chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.animezone.R
import kotlinx.android.synthetic.main.mensajes_chat.view.*
import java.text.SimpleDateFormat

class MensajeAdapter(private val usuario: String): RecyclerView.Adapter<MensajeAdapter.MessageViewHolder>() {
    private var mensajes: List<Mensaje> = emptyList()

    fun setData(list: List<Mensaje>){
        mensajes = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.mensajes_chat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        //Recibimos un mensaje
        val mensaje = mensajes[position]
        val fechaFormateada = SimpleDateFormat("dd/M/yyyy hh:mm")
        //Si el mensaje es mio lo pone verde y de mi lado derecho
        if(usuario == mensaje.from){
            //Que se vea verde
            holder.itemView.miMensajeLayout.visibility = View.VISIBLE
            holder.itemView.otroMensajeLayout.visibility = View.GONE
            holder.itemView.miMensajeTextView.text = mensaje.mensaje
            holder.itemView.fecha_mensajePropio.text=fechaFormateada.format(mensaje.fecha)
        } else {
            //Si es del otro usuario lo pone azul y en el otro lado
            holder.itemView.miMensajeLayout.visibility = View.GONE
            holder.itemView.otroMensajeLayout.visibility = View.VISIBLE
            holder.itemView.otroMensajeTextView.text = mensaje.mensaje
            holder.itemView.fecha_mensajeAjeno.text=fechaFormateada.format(mensaje.fecha)
        }
    }

    override fun getItemCount(): Int {
        return mensajes.size
    }

    class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}