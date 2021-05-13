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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animezone.Perfil.PerfilAjenoActivity
import com.example.animezone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.card_post.view.*
import java.text.SimpleDateFormat

class PublicacionAdapter(private val activity: Activity, private val dataset: List<Publicacion>) : RecyclerView.Adapter<PublicacionAdapter.ViewHolder>() {
    //Variables Firebase
    private val autentificacion = FirebaseAuth.getInstance()
    private val basedeDatos = Firebase.firestore
    //Este es el ViewHolder, que a su vez le paso el layout completo
    class ViewHolder(val layout: View) : RecyclerView.ViewHolder(layout)

    //Esto lo que crea es mi layout y retorna mi ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.card_post, parent, false)

        return ViewHolder(
            layout
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

        holder.layout.megustaContador.text="${likes.size} Me gusta"
        holder.layout.nombrePersonatv.text = publicacion.usuarioNombre
        holder.layout.descripcion_tv.text = publicacion.post
        holder.layout.fecha_tv.text = fechaFormateada.format(publicacion.fecha)
        //Asi se llama a la imagen publicada
        Glide.with(activity)
            .load(publicacion.foto)
            .into(holder.layout.imagenPublicacion)
        //Coge siempre la foto de perfil del usuario
        basedeDatos.collection("Usuarios").document(publicacion.usuarioNombre.toString()).get()
            .addOnSuccessListener {
                //Aqui llamo a la imagen del perfil del usuario
                Glide.with(activity).load(it.getString("imagen").toString()).fitCenter().into(holder.layout.imagenPerfilMenu)
        }

        //Le paso por parametro el boton del CardView y la lista de la gente con sus uids
        cambiarColor(genteLikes ,holder.layout.like_btn)


        //Si la gente le gusta y le da al boton
        holder.layout.like_btn.setOnClickListener {
            genteLikes=!genteLikes
            cambiarColor(genteLikes ,holder.layout.like_btn)
            //Si la gente le da like se añade el id del usuario a la lista de me gusta que tiene la publicacion
            if(genteLikes){
                likes.add(autentificacion.currentUser.displayName!!)
                /*Toast.makeText(activity,holder.itemView.toString(), Toast.LENGTH_SHORT).show()
                Toast.makeText(activity,holder.adapterPosition.toString(), Toast.LENGTH_SHORT).show()*/
            }
            //Si la gente que le dio like le da de nuevo al boton se quitara de la lista de likes de la publicacion
            else{
                likes.remove(autentificacion.currentUser.displayName)
                //Toast.makeText(activity,holder.layoutPosition.toString(), Toast.LENGTH_SHORT).show()
            }
            //Esto de aqui es por si un usuario que esta tambien conectado le da a la vez que tu like a una publicacion
            //Entonces lo que se hace aqui abajo es actualizarlo instantaneamente el numero de likes, del campo likes en la base de datos
            val doc=basedeDatos.collection("Publicaciones").document(publicacion.uid!!)
            basedeDatos.runTransaction {
                it.update(doc,"likes",likes)
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



        //Cuando le des al boton compartir se abrirá las aplicaciones... y podre compartir la publicacion (Creo q solo el texto)
        holder.layout.compartir_btn.setOnClickListener {
            val enviarIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, publicacion.post)
                type = "text/plain"
            }
            val compartirIntent = Intent.createChooser(enviarIntent, null)
            activity.startActivity(compartirIntent)
        }


        //Si en una publicacion la mantienes presionada (setOnLongClickListener)
        //Si es tu propia publicacion, que puedas borrarlo sino, no podrás
        if(publicacion.usuarioNombre==autentificacion.currentUser!!.displayName) {
            holder.layout.setOnLongClickListener {
                //Dialogo para que este seguro si quiere borrar la publicacion
                AlertDialog.Builder(activity).apply {
                    setTitle("Borrar Publicacion")
                    setMessage("La publicacion será borrado permanentemente, ¿Estás Seguro?")
                    //Si le da que "Si" el usuario lo borrará el id de la coleccion publicaciones por lo q se borrará
                    setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Si</font>")){ dialogInterface: DialogInterface, i: Int ->
                        basedeDatos.collection("Publicaciones").document(publicacion.uid!!).delete()
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener {
                            }
                    }
                    //Si le da que "No" no hará nada
                    setNegativeButton(Html.fromHtml("<font color='#FFFFFF'>No</font>"),null)
                }.show()
                return@setOnLongClickListener true
            }
        }
    }

    private fun irPerfilAjeno(holder: ViewHolder) {
        var nombreUsuario: String = holder.layout.nombrePersonatv.text.toString()
        val intent = Intent(activity, PerfilAjenoActivity::class.java)
        intent.putExtra("UsuarioInfo", nombreUsuario)
        startActivity(activity, intent, Bundle())
    }


    private fun cambiarColor(gustado:Boolean, botonMegusta: Button) {
        //Si le di me gusta a la publicacion tendra un color
        if(gustado){
            botonMegusta.setTextColor(ContextCompat.getColor(activity,
                R.color.blanco
            ))
        }
        //Sino me gusta la publicacion tendrá color negro el boton de Me gusta
        else{
            botonMegusta.setBackgroundColor(Color.GRAY)
        }
    }
}