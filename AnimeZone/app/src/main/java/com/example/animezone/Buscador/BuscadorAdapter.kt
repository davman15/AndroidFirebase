package com.example.animezone.Buscador

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animezone.Clase.Usuario
import com.example.animezone.R
import kotlinx.android.synthetic.main.chat_item.view.*
import kotlinx.android.synthetic.main.lista_seguidores.view.*
import java.util.stream.Collectors

class BuscadorAdapter(val usuarioClick: (Usuario) -> Unit) :
    RecyclerView.Adapter<BuscadorAdapter.BuscadorViewHolder>() {
    var usuariosLista: List<Usuario> = emptyList()
    var originalUsuarios:List<Usuario> = emptyList()

    //Metodo que sirve para que el usuario pueda encontrar el perfil que quiera
    fun filtradorUsuarios(lista: MutableList<Usuario>,caracterBuscador: String) {
        usuariosLista = lista
        if (caracterBuscador.isEmpty()) {
            (usuariosLista as MutableList<Usuario>).clear()

            (usuariosLista as MutableList<Usuario>).addAll(originalUsuarios!!)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val collect = usuariosLista!!.stream().filter { i: Usuario ->
                    i.usuarioId!!.toLowerCase().contains(caracterBuscador) || i.usuarioId!!.contains(caracterBuscador)
                }
                    .collect(Collectors.toList())
                (usuariosLista as MutableList<Usuario>).clear()
                (usuariosLista as MutableList<Usuario>).addAll(collect)
            }
        }
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuscadorViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.lista_seguidores, parent, false)
        return BuscadorViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return usuariosLista.size
    }

    override fun onBindViewHolder(holder: BuscadorViewHolder, position: Int) {
        val usuarios = usuariosLista[position]
        holder.itemView.nombre_Seguidor.text=usuarios.usuarioId
        Glide.with(holder.itemView.context)
            .load(usuarios.imagen)
            .fitCenter()
            .into(holder.itemView.foto_Seguidor)

        holder.itemView.setOnClickListener {
            usuarioClick(usuarios)
        }
    }

    class BuscadorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}