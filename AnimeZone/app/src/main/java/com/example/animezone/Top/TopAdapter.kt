package com.example.animezone.Top

import android.content.DialogInterface
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animezone.Chat.Chat
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.top_anime.view.*


class TopAdapter  (val usuarioClick: (Top) -> Unit): RecyclerView.Adapter<TopAdapter.TopViewHolder>() {
    var animesLista: List<Top> = emptyList()
    private val baseDatos = Firebase.firestore
    private var autentificacion = Firebase.auth
    fun colocarAnime(lista: List<Top>) {
        animesLista = lista
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.top_anime, parent, false)
        return TopViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return animesLista.size
    }

    override fun onBindViewHolder(holder: TopViewHolder, position: Int) {
        val animes=animesLista[position]
        holder.itemView.titulo_animeTop.text = animes.nombreAnime.toString()
        holder.itemView.descripcion_animeTop.text = animes.descripcion.toString()
        holder.itemView.nota_animeTop.text = animes.nota.toString()
        holder.itemView.episodios_animeTop.text = "Episodios: ${animes.episodios.toString()}"
        Glide.with(holder.itemView.context).load(animes.imagenAnime).into(holder.itemView.imagen_animeTop)

        holder.itemView.enlace_animeTop.setOnClickListener {
            val intent= Intent(holder.itemView.context,MostrarInformacionActivity::class.java)
            intent.putExtra("url",animes.enlaceAnimeList.toString())
            holder.itemView.context.startActivity(intent)
        }


        //Eliminar
        holder.itemView.eliminar_animeTop.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context).apply {
                setTitle("Borrar Anime")
                setMessage("El anime se borrará de tu top actual, ¿está seguro?")
                //Si le da que "Si" el usuario lo borrará el id de la coleccion publicaciones por lo q se borrará
                setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Si</font>")) { dialogInterface: DialogInterface, i: Int ->
                    baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName)
                        .collection("TopAnime").document(animes.nombreAnime.toString()).delete()
                }

                setNegativeButton(Html.fromHtml("<font color='#FFFFFF'>No</font>"), null)
            }.show()
        }
        holder.itemView.setOnClickListener {
            usuarioClick(animes)
        }
    }
    class TopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}