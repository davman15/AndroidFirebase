package com.example.animezone.Publicaciones

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.animezone.Notificaciones.Notificacion
import com.example.animezone.Perfil.PerfilActivity
import com.example.animezone.Perfil.PerfilAjenoActivity
import com.example.animezone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.card_post.view.*
import java.text.SimpleDateFormat
import java.util.*


class PublicacionAdapter(private val activity: Activity, private var dataset: List<Publicacion>) :
    RecyclerView.Adapter<PublicacionAdapter.ViewHolder>() {
    //Variables Firebase
    private val autentificacion = FirebaseAuth.getInstance()
    private val basedeDatos = Firebase.firestore
    private var favorito = false

    class ViewHolder(val layout: View) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_post, parent, false))
    }

    //Aqui recogemos el numero de publicaciones q habra, la cantidad del array de Publicacion
    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fechaFormateada = SimpleDateFormat("dd/M/yyyy hh:mm a")
        val publicacion = dataset[position]
        val likes = publicacion.likes!!.toMutableList()
        var genteLikes = likes.contains(autentificacion.currentUser.displayName)

        holder.layout.megustaContador.text = "${likes.size} Me gusta"
        holder.layout.nombrePersonatv.text = publicacion.usuarioNombre
        holder.layout.descripcion_tv.text = publicacion.post
        holder.layout.fecha_tv.text = fechaFormateada.format(publicacion.fecha)
        holder.layout.titulo_tv.text = publicacion.titulo
        //Asi se llama a la imagen publicada
        Glide.with(holder.itemView.context)
            .load(publicacion.foto)
            .into(holder.layout.imagenPublicacion)

        //Coge siempre la foto de perfil del usuario
        basedeDatos.collection("Usuarios").document(publicacion.usuarioNombre.toString()).get()
            .addOnSuccessListener {
                //Aqui llamo a la imagen del perfil del usuario
                Glide.with(holder.itemView.context).load(it?.getString("imagen").toString()).fitCenter()
                    .into(holder.layout.imagenPerfilMenu)
            }
        likeAnimacionCargaPrevia(holder.layout.like_btn,R.raw.bandai_dokkan,genteLikes)

        //Si la gente le gusta y le da al boton
        holder.layout.like_btn.setOnClickListener {
            genteLikes = !genteLikes
            likeAnimacionAlDarle(holder.layout.like_btn,R.raw.bandai_dokkan,genteLikes)

            //Si la gente le da like se añade el id del usuario a la lista de me gusta que tiene la publicacion
            if (genteLikes) {
                likes.add(autentificacion.currentUser.displayName!!)
                actualizarLikes(publicacion, likes)
                holder.layout.megustaContador.text = "${likes.size} Me gusta"

                if(holder.layout.nombrePersonatv.text.toString()!=autentificacion.currentUser.displayName)
                    enviarNotificacion(publicacion.usuarioNombre.toString())

            } else {
                likes.remove(autentificacion.currentUser.displayName)
                actualizarLikes(publicacion, likes)
                holder.layout.megustaContador.text = "${likes.size} Me gusta"
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
            eliminarAnadirFavoritos(publicacion, holder,R.raw.animacion_star)
        }

    }

    private fun actualizarLikes(
        publicacion: Publicacion,
        likes: MutableList<String>
    ) {
        val referencia = basedeDatos.collection("Publicaciones").document(publicacion.uid!!)
        basedeDatos.runTransaction {
            it.update(referencia, "likes", likes)
            null
        }
    }

    private fun enviarNotificacion(otroUsuario: String) {
        var fechaId = ""
        val referenciaUsuarios = basedeDatos.collection("Usuarios")
        val fechaFormateada = SimpleDateFormat("dd-M-yyyy hh:mm:ss")
        val notificacion = Notificacion(
            usuarioId = autentificacion.currentUser.displayName,
            mensaje = " te ha dado un like en tu publicación.",
            fecha = Date(),
            id = autentificacion.currentUser.displayName+"-"+fechaFormateada.format(Date())
        )
        fechaId = fechaFormateada.format(notificacion.fecha)
        referenciaUsuarios.document(otroUsuario).collection("Notificaciones")
            .document(notificacion.usuarioId.toString() + "-" + fechaId)
            .set(notificacion).addOnSuccessListener {
                referenciaUsuarios.document(otroUsuario).collection("Notificaciones No Leidas")
                    .document(notificacion.usuarioId.toString() + "-" + fechaId).set(notificacion)
            }
    }

    private fun eliminarAnadirFavoritos(publicacion: Publicacion, holder: ViewHolder,animacion: Int) {
        var referenciaFavoritos = basedeDatos.collection("Usuarios")
            .document(autentificacion.currentUser.displayName)
            .collection("Favoritos").document(publicacion.uid!!)

        referenciaFavoritos.get().addOnSuccessListener {

            if (it.exists()) {
                basedeDatos.collection("Usuarios")
                    .document(autentificacion.currentUser.displayName)
                    .collection("Favoritos").document(publicacion.uid!!).delete()
                    .addOnSuccessListener {
                        Toast.makeText(holder.itemView.context, "Eliminado de Favoritos", Toast.LENGTH_SHORT).show()
                        holder.itemView.anadirFavorito_btn.animate().alpha(0f).setDuration(300)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    holder.itemView.anadirFavorito_btn.setImageResource(R.drawable.estrella_icono)
                                    holder.itemView.anadirFavorito_btn.alpha = 1f
                                }
                            })
                    }
            } else {
                referenciaFavoritos.set(publicacion)
                    .addOnSuccessListener {
                            Toast.makeText(holder.itemView.context, "Añadido a Favoritos", Toast.LENGTH_SHORT).show()
                            holder.itemView.anadirFavorito_btn.setAnimation(animacion)
                            holder.itemView.anadirFavorito_btn.playAnimation()
                    }
            }
        }
    }


    private fun existenciaFavoritos(publicacion: Publicacion, holder: ViewHolder) {
        basedeDatos.collection("Usuarios").document(autentificacion.currentUser.displayName)
            .collection("Favoritos").document(publicacion.uid!!).get().addOnSuccessListener {
                favorito = it.exists()
                cambiarColorFavorito2(favorito, holder.itemView.anadirFavorito_btn,R.raw.animacion_star)
            }
    }

    private fun irPerfilAjeno(holder: ViewHolder) {
        var nombreUsuario: String = holder.layout.nombrePersonatv.text.toString()
        val intent = Intent(activity, PerfilAjenoActivity::class.java)
        val intentoPropio = Intent(activity, PerfilActivity::class.java)
        intent.putExtra("UsuarioChat", nombreUsuario)
        if (nombreUsuario == autentificacion.currentUser.displayName)
            startActivity(activity, intentoPropio, Bundle())
        else
            startActivity(activity, intent, Bundle())
    }

    private fun likeAnimacionAlDarle(imageView: LottieAnimationView, animacion:Int, darLike:Boolean):Boolean{
        if(darLike){
            imageView.setAnimation(animacion)
            imageView.playAnimation()
        }else{
            imageView.animate().alpha(0f).setDuration(300).setListener(object :AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    imageView.setImageResource(R.drawable.imagen_like)
                    imageView.alpha=1f
                }
            })
        }
        return darLike
    }

    //Este metodo es que para que cuando el usuario se meta de nuevo y si le dio like solo tenga
    private fun likeAnimacionCargaPrevia(imageView: LottieAnimationView, animacion:Int, darLike:Boolean):Boolean{
        if(darLike){
            imageView.setAnimation(animacion)
            imageView.playAnimation()
        }else{
             imageView.setImageResource(R.drawable.imagen_like)
        }
        return darLike
    }

    private fun cambiarColorFavorito2(darFavorito: Boolean, botonFavorito: LottieAnimationView,animacion:Int) {
        //Si le di me gusta a la publicacion tendra un color
        if (darFavorito){
            botonFavorito.setAnimation(animacion)
            botonFavorito.playAnimation()
        }
        //Sino me gusta la publicacion tendrá color negro el boton de Me gusta
        else {
            botonFavorito.setImageResource(R.drawable.estrella_icono)
        }
    }

    private fun cambiarColorFavorito(darFavorito: Boolean, botonFavorito: LottieAnimationView,animacion:Int) {
        //Si le di me gusta a la publicacion tendra un color
        if (darFavorito){
            println("Lo añade")
            botonFavorito.setAnimation(animacion)
            botonFavorito.playAnimation()
        }

        //Sino me gusta la publicacion tendrá color negro el boton de Me gusta
        else {
            println("Lo quita")
            botonFavorito.animate().alpha(0f).setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        botonFavorito.setImageResource(R.drawable.estrella_icono)
                        botonFavorito.alpha = 1f
                    }
                })
        }
    }
}