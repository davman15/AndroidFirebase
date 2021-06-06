package com.example.animezone.Top

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animezone.R
import kotlinx.android.synthetic.main.top_anime_ajeno.view.*

class TopAjenoAdapter (val usuarioClick: (Top) -> Unit): RecyclerView.Adapter<TopAjenoAdapter.TopAjenoViewHolder>() {
    var animesLista: List<Top> = emptyList()

    fun colocarAnime(lista: List<Top>) {
        animesLista = lista
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopAjenoViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.top_anime_ajeno, parent, false)
        return TopAjenoViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return animesLista.size
    }

    override fun onBindViewHolder(holder: TopAjenoViewHolder, position: Int) {
        val animes=animesLista[position]
        holder.itemView.titulo_animeTop_ajeno.text = animes.nombreAnime.toString()
        holder.itemView.descripcion_animeTop_Ajeno.text = animes.descripcion.toString()
        holder.itemView.nota_animeTop_Ajeno.text = animes.nota.toString()
        holder.itemView.episodios_animeTop_Ajeno.text = "Episodios: ${animes.episodios.toString()}"
        Glide.with(holder.itemView.context).load(animes.imagenAnime).into(holder.itemView.imagen_animeTop_Ajeno)

        holder.itemView.enlace_animeTop_Ajeno.setOnClickListener {
            val intent= Intent(holder.itemView.context,MostrarInformacionActivity::class.java)
            intent.putExtra("url",animes.enlaceAnimeList.toString())
            holder.itemView.context.startActivity(intent)
        }
        holder.itemView.setOnClickListener {
            usuarioClick(animes)
        }

    }
    class TopAjenoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}