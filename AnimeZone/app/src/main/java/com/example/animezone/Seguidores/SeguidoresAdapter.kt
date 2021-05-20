package com.example.animezone.Seguidores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animezone.Clase.Usuario
import com.example.animezone.R
import kotlinx.android.synthetic.main.lista_seguidores.view.*

class SeguidoresAdapter(val seguidorClick: (Usuario) -> Unit) :
    RecyclerView.Adapter<SeguidoresAdapter.SeguidoresViewHolder>() {
    var listaSeguidores: List<Usuario> = emptyList()

    fun listaActualizada(lista: MutableList<Usuario>) {
        listaSeguidores = lista
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeguidoresViewHolder {
        return SeguidoresViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.lista_seguidores, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listaSeguidores.size
    }

    override fun onBindViewHolder(holder: SeguidoresViewHolder, position: Int) {
        val usuarios = listaSeguidores[position]
            holder.itemView.nombre_Seguidor.text = usuarios.usuarioId
            Glide.with(holder.itemView.context).load(usuarios.imagen).fitCenter()
                .into(holder.itemView.foto_Seguidor)
            holder.itemView.setOnClickListener {
                seguidorClick(usuarios)
        }

    }

    class SeguidoresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}