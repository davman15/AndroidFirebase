package com.example.animezone.PublicacionesFavoritas

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animezone.Perfil.PerfilActivity
import com.example.animezone.Perfil.PerfilAjenoActivity
import com.example.animezone.Publicaciones.Publicacion
import com.example.animezone.Publicaciones.PublicacionAdapter
import com.example.animezone.R
import com.example.animezone.TusPublicaciones.TusPublicacionesActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.card_post.view.*
import kotlinx.android.synthetic.main.cards_favoritas.view.*
import java.text.SimpleDateFormat

class PublicacionesFavoritasAdapter(
    private val activity: Activity,
    private var listaPublicacionesFavoritas: List<Publicacion>
) :
    RecyclerView.Adapter<PublicacionesFavoritasAdapter.PublicacionFavoritasViewHolder>() {
    private var baseDatos = Firebase.firestore
    private val autentificacion = Firebase.auth

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PublicacionFavoritasViewHolder {
        return PublicacionFavoritasViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cards_favoritas, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listaPublicacionesFavoritas.size
    }

    override fun onBindViewHolder(holder: PublicacionFavoritasViewHolder, position: Int) {
        val fechaFormateada = SimpleDateFormat("dd/M/yyyy hh:mm a")
        val publicaciones = listaPublicacionesFavoritas[position]

        holder.itemView.nombrePersonaFavorito_tv.text = publicaciones.usuarioNombre
        holder.itemView.fechaFavorito_tv.text = fechaFormateada.format(publicaciones.fecha)
        holder.itemView.descripcionFavorito_tv.text = publicaciones.post
        holder.itemView.tituloFavorito_tv.text = publicaciones.titulo
        Glide.with(holder.itemView.context)
            .load(publicaciones.foto)
            .into(holder.itemView.imagenPublicacionFavorito)

        baseDatos.collection("Usuarios").document(publicaciones.usuarioNombre.toString())
            .addSnapshotListener { snapshot, e ->
                //Aqui llamo a la imagen del perfil del usuario
                Glide.with(activity.baseContext).load(snapshot?.getString("imagen").toString())
                    .fitCenter()
                    .into(holder.itemView.imagenPerfilFavorito)
            }

        borrarPublicacionesFavoritas(holder, publicaciones)

        holder.itemView.imagenPerfilFavorito.setOnClickListener {
            irPerfilAjeno(holder)
        }
        holder.itemView.nombrePersonaFavorito_tv.setOnClickListener {
            irPerfilAjeno(holder)
        }
    }

    private fun borrarPublicacionesFavoritas(
        holder: PublicacionFavoritasViewHolder,
        publicaciones: Publicacion
    ) {
        holder.itemView.setOnLongClickListener {
                //Dialogo para que este seguro si quiere borrar la publicacion
                AlertDialog.Builder(holder.itemView.context).apply {
                    setTitle("Borrar Publicacion")
                    setMessage("La publicacion será borrada permanentemente de tus lista de Favoritos, ¿Estás Seguro?")
                    //Si le da que "Si" el usuario lo borrará el id de la coleccion publicaciones por lo q se borrará
                    setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Si</font>")) { dialogInterface: DialogInterface, i: Int ->
                        baseDatos.collection("Usuarios")
                            .document(autentificacion.currentUser.displayName).collection("Favoritos")
                            .document(publicaciones.uid!!).delete()
                            .addOnSuccessListener {
                            }
                    }
                    //Si le da que "No" no hará nada
                    setNegativeButton(Html.fromHtml("<font color='#FFFFFF'>No</font>"), null)
                }.show()
            return@setOnLongClickListener true
        }
    }

    private fun irPerfilAjeno(holder: PublicacionFavoritasViewHolder) {
        var nombreUsuario: String = holder.itemView.nombrePersonaFavorito_tv.text.toString()
        val intent = Intent(holder.itemView.context, PerfilAjenoActivity::class.java)
        val intentoPropio = Intent(activity, PerfilActivity::class.java)
        intent.putExtra("UsuarioChat", nombreUsuario)
        if (nombreUsuario == autentificacion.currentUser.displayName)
            startActivity(activity, intentoPropio, Bundle())
        else
            startActivity(activity, intent, Bundle())
    }

    class PublicacionFavoritasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}