package com.example.animezone.Notificaciones

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.notificaciones_card.view.*
import java.text.SimpleDateFormat

class NotificacionAdapter(val notificacionClick: (Notificacion) -> Unit) :
    RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder>() {
    var notificaciones: List<Notificacion> = emptyList()
    private var autentificacion = Firebase.auth
    private var baseDatos = Firebase.firestore

    fun listaActualizadaNotificaciones(lista: MutableList<Notificacion>) {
        notificaciones = lista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacionViewHolder {
        return NotificacionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notificaciones_card, parent, false))
    }

    override fun getItemCount(): Int {
        return notificaciones.size
    }

    override fun onBindViewHolder(holder:NotificacionViewHolder, position: Int) {
        val fechaFormateada = SimpleDateFormat("dd/M/yyyy hh:mm a")
        val listaNotificaciones = notificaciones[position]
        var autorNotificacion = listaNotificaciones.usuarioId
        actualizarFotoUsuario(listaNotificaciones, holder)

        holder.itemView.mensaje_Notificaciones_tv.setText(Html.fromHtml("<b>$autorNotificacion</b>${listaNotificaciones.mensaje}"))
        holder.itemView.fecha_notificaciones_tv.setText(fechaFormateada.format(listaNotificaciones.fecha))

        holder.itemView.setOnClickListener {
            notificacionClick(listaNotificaciones)
        }
    }

    private fun actualizarFotoUsuario(
        listaNotificaciones: Notificacion,
        holder: NotificacionViewHolder
    ) {
        baseDatos.collection("Usuarios").document(listaNotificaciones.usuarioId.toString())
            .addSnapshotListener { value, error ->
                Glide.with(holder.itemView.context)
                    .load(value?.getString("imagen").toString())
                    .into(holder.itemView.imagenAutorNotificacion)
            }
    }

    class NotificacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}