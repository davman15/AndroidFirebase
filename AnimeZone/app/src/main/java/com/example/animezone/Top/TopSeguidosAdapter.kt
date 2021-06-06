package com.example.animezone.Top

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animezone.Clase.Usuario
import com.example.animezone.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.lista_seguidores.view.*


class TopSeguidosAdapter (val seguidoClick: (Usuario) -> Unit) :
    RecyclerView.Adapter<TopSeguidosAdapter.TopSeguidosViewHolder>(){
    private val baseDatos = Firebase.firestore
    var listaSeguidos: List<Usuario> = emptyList()

    fun listaActualizada(lista: MutableList<Usuario>) {
        listaSeguidos = lista
        notifyDataSetChanged()
    }

    class TopSeguidosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopSeguidosViewHolder {
        return TopSeguidosViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.lista_seguidores, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listaSeguidos.size
    }

    private fun actualizarFotoSeguidor(usuarios: Usuario, holder: TopSeguidosViewHolder) {
        baseDatos.collection("Usuarios").document(usuarios.usuarioId.toString())
            .addSnapshotListener { value, error ->
                Glide.with(holder.itemView.context).load(value?.getString("imagen").toString())
                    .fitCenter()
                    .into(holder.itemView.foto_Seguidor)
            }
    }

    override fun onBindViewHolder(holder: TopSeguidosViewHolder, position: Int) {
        val seguidos=listaSeguidos[position]
        holder.itemView.nombre_Seguidor.setText(seguidos.usuarioId)
        actualizarFotoSeguidor(seguidos,holder)
        holder.itemView.setOnClickListener {
            seguidoClick(seguidos)
        }
    }
}