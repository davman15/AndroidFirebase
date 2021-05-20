package com.example.animezone.Publicaciones

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animezone.Perfil.PerfilActivity
import com.example.animezone.Perfil.PerfilAjenoActivity
import com.example.animezone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.card_post.view.*
import java.text.SimpleDateFormat

class PublicacionAdapter(private val activity: Activity, private var dataset: List<Publicacion>) :
    RecyclerView.Adapter<PublicacionAdapter.ViewHolder>() {
    //Variables Firebase
    private val autentificacion = FirebaseAuth.getInstance()
    private val basedeDatos = Firebase.firestore
    private var favorito = false

    class ViewHolder(val layout: View) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_post, parent, false)
        )

    }

    //Aqui recogemos el numero de publicaciones q habra, la cantidad del array de Publicacion
    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fechaFormateada = SimpleDateFormat("dd/M/yyyy hh:mm a")
        val publicacion = dataset[position]
        //Obtener los likes, esto es un array
        val likes = publicacion.likes!!.toMutableList()
        //Variable donde identificaremos a la gente q le dio like a la publicacion
        var genteLikes = likes.contains(autentificacion.currentUser.displayName)

        holder.layout.megustaContador.text = "${likes.size} Me gusta"
        holder.layout.nombrePersonatv.text = publicacion.usuarioNombre
        holder.layout.descripcion_tv.text = publicacion.post
        holder.layout.fecha_tv.text = fechaFormateada.format(publicacion.fecha)
        holder.layout.titulo_tv.text = publicacion.titulo
        //Asi se llama a la imagen publicada
        Glide.with(activity)
            .load(publicacion.foto)
            .into(holder.layout.imagenPublicacion)
        //Coge siempre la foto de perfil del usuario
        basedeDatos.collection("Usuarios").document(publicacion.usuarioNombre.toString())
            .addSnapshotListener { snapshot, e ->
                //Aqui llamo a la imagen del perfil del usuario
                Glide.with(activity).load(snapshot?.getString("imagen").toString()).fitCenter()
                    .into(holder.layout.imagenPerfilMenu)
            }


        //Le paso por parametro el boton del CardView y la lista de la gente con sus uids
        cambiarColor(genteLikes, holder.layout.like_btn)


        //Si la gente le gusta y le da al boton
        holder.layout.like_btn.setOnClickListener {
            genteLikes = !genteLikes
            cambiarColor(genteLikes, holder.layout.like_btn)
            //Si la gente le da like se añade el id del usuario a la lista de me gusta que tiene la publicacion
            if (genteLikes) {
                likes.add(autentificacion.currentUser.displayName!!)
                /*Toast.makeText(activity,holder.itemView.toString(), Toast.LENGTH_SHORT).show()
                Toast.makeText(activity,holder.adapterPosition.toString(), Toast.LENGTH_SHORT).show()*/
            } else {
                likes.remove(autentificacion.currentUser.displayName)
            }
            //Esto de aqui es por si un usuario que esta tambien conectado le da a la vez que tu like a una publicacion
            //Entonces lo que se hace aqui abajo es actualizarlo instantaneamente el numero de likes, del campo likes en la base de datos
            val doc = basedeDatos.collection("Publicaciones").document(publicacion.uid!!)
            basedeDatos.runTransaction {
                it.update(doc, "likes", likes)
                null
            }
        }

        //Esto es para mostrar el perfil al darle click a la foto de Perfil
        holder.layout.imagenPerfilMenu.setOnClickListener {
            irPerfilAjeno(holder)
        }
        //Lo mismo que arriba pero es por si le da al nombre o a la foto de Perfil
        holder.layout.nombrePersonatv.setOnClickListener {
            irPerfilAjeno(holder)
        }


        //Aqui controlo si cada publicacion se encuentra en la lista de favoritos del usuario
        existenciaFavoritos(publicacion, holder)


        holder.layout.anadirFavorito_btn.setOnClickListener {
            //Aqui controlo si cada publicacion se encuentra en la lista de favoritos del usuario
            eliminarAnadirFavoritos(publicacion, holder)
        }

        //Si en una publicacion la mantienes presionada (setOnLongClickListener)
        //Si es tu propia publicacion, que puedas borrarlo sino, no podrás
        borrarPropiaPublicacion(publicacion, holder)
    }

    private fun eliminarAnadirFavoritos(
        publicacion: Publicacion,
        holder: ViewHolder
    ) {
        var referenciaFavoritos=basedeDatos.collection("Usuarios")
            .document(autentificacion.currentUser.displayName)
            .collection("Favoritos").document(publicacion.uid!!)

        referenciaFavoritos.get().addOnSuccessListener {
                if (it.exists()) {
                    basedeDatos.collection("Usuarios")
                        .document(autentificacion.currentUser.displayName)
                        .collection("Favoritos").document(publicacion.uid!!).delete()
                        .addOnSuccessListener {
                            holder.itemView.anadirFavorito_btn.setBackgroundColor(Color.GRAY)
                            Toast.makeText(
                                holder.itemView.context,
                                "Eliminado de Favoritos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    referenciaFavoritos.set(publicacion)
                        .addOnSuccessListener {
                            holder.itemView.anadirFavorito_btn.setBackgroundResource(R.drawable.button_rounded)
                            Toast.makeText(
                                holder.itemView.context,
                                "Añadido a Favoritos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
    }

    private fun borrarPropiaPublicacion(publicacion: Publicacion, holder: ViewHolder) {
        if (publicacion.usuarioNombre == autentificacion.currentUser!!.displayName) {
            holder.layout.setOnLongClickListener {
                //Dialogo para que este seguro si quiere borrar la publicacion
                AlertDialog.Builder(activity).apply {
                    setTitle("Borrar Publicacion")
                    setMessage("La publicacion será borrada permanentemente, ¿Estás Seguro?")
                    //Si le da que "Si" el usuario lo borrará el id de la coleccion publicaciones por lo q se borrará
                    setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Si</font>")) { dialogInterface: DialogInterface, i: Int ->
                        basedeDatos.collection("Publicaciones").document(publicacion.uid!!).delete()
                            .addOnSuccessListener {
                            }
                    }
                    //Si le da que "No" no hará nada
                    setNegativeButton(Html.fromHtml("<font color='#FFFFFF'>No</font>"), null)
                }.show()
                return@setOnLongClickListener true
            }
        }
    }

    private fun existenciaFavoritos(publicacion: Publicacion, holder: ViewHolder) {
        basedeDatos.collection("Usuarios").document(autentificacion.currentUser.displayName)
            .collection("Favoritos").document(publicacion.uid!!).get().addOnSuccessListener {
                favorito = it.exists()
                cambiarColorFavorito(favorito, holder.itemView.anadirFavorito_btn)
            }
    }

    private fun irPerfilAjeno(holder: ViewHolder) {
        var nombreUsuario: String = holder.layout.nombrePersonatv.text.toString()
        val intent = Intent(activity, PerfilAjenoActivity::class.java)
        val intentoPropio = Intent(activity, PerfilActivity::class.java)
        intent.putExtra("UsuarioInfo", nombreUsuario)
        if (nombreUsuario == autentificacion.currentUser.displayName)
            startActivity(activity, intentoPropio, Bundle())
        else
            startActivity(activity, intent, Bundle())
    }


    private fun cambiarColor(gustado: Boolean, botonMegusta: Button) {
        //Si le di me gusta a la publicacion tendra un color
        if (gustado)
            botonMegusta.setTextColor(ContextCompat.getColor(activity, R.color.blanco))
        //Sino me gusta la publicacion tendrá color negro el boton de Me gusta
        else
            botonMegusta.setBackgroundColor(Color.GRAY)
    }

    private fun cambiarColorFavorito(darFavorito: Boolean, botonFavorito: Button) {
        //Si le di me gusta a la publicacion tendra un color
        if (darFavorito) {
            botonFavorito.setBackgroundResource(R.drawable.button_rounded)
        }
        //Sino me gusta la publicacion tendrá color negro el boton de Me gusta
        else {
            botonFavorito.setBackgroundColor(Color.GRAY)
        }
    }
}