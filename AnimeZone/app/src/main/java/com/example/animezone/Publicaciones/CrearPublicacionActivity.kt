package com.example.animezone.Publicaciones

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.animezone.Notificaciones.Notificacion
import com.example.animezone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_crear_publicacion.*
import java.text.SimpleDateFormat
import java.util.*

class CrearPublicacionActivity : AppCompatActivity() {
    //Variables Firebase
    private val autentificacion = FirebaseAuth.getInstance()
    private val baseDatos = FirebaseFirestore.getInstance()
    private val basedeDatos = Firebase.firestore

    //Raiz del Storage
    private val storageReferencia = Firebase.storage.getReference("Publicaciones")
    private var imagenUri: Uri? = null

    //Codigo el que queramos
    private val file = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_publicacion)

        foto_Publicacion.setOnClickListener {
            seleccionarFotoGaleria()
        }

        publicar_btn.setOnClickListener {
            crearPublicacion_circulo2.visibility = View.VISIBLE
            crearPublicacion_circulo1.visibility = View.VISIBLE
            if (titulo_publicacion.text.toString() == "") {
                crearPublicacion_circulo2.visibility = View.INVISIBLE
                crearPublicacion_circulo1.visibility = View.INVISIBLE
                AlertDialog.Builder(this).apply {
                    setTitle("Título Obligatorio")
                    setMessage("Añada un título a su publicación")
                    setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>")) { dialogInterface: DialogInterface, i: Int ->
                        null
                    }
                }.show()
                return@setOnClickListener
            }

            if (descripcion_publicacion.text.toString() == "") {
                crearPublicacion_circulo2.visibility = View.INVISIBLE
                crearPublicacion_circulo1.visibility = View.INVISIBLE
                AlertDialog.Builder(this).apply {
                    setTitle("Descripción Obligatoria")
                    setMessage("Añada una descripción a su publicación")
                    setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>")) { dialogInterface: DialogInterface, i: Int ->
                        null
                    }
                }.show()
                return@setOnClickListener
            }
            //Selecciono el Uri de la imagen elegida por el usuario
            var fileUri = imagenUri
            if (fileUri == null) {
                crearPublicacion_circulo2.visibility = View.INVISIBLE
                crearPublicacion_circulo1.visibility = View.INVISIBLE
                AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    setMessage("Seleccione una foto para hacer una publicación")
                    setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Si</font>")) { dialogInterface: DialogInterface, i: Int ->
                        null
                    }
                }.show()
                return@setOnClickListener
            }
            subirPublicacion(fileUri)
        }
    }

    private fun subirPublicacion(fileUri: Uri) {
        //Aqui creo la subcarpeta de la referencia
        val folder = storageReferencia.child("imagenPublicacion/${fileUri!!.lastPathSegment}")
        //Lo subimos al Storage
        folder.putFile(fileUri).addOnSuccessListener {
            //Si se sube bien al Storage, Creo el link con downloadURL
            folder.downloadUrl.addOnSuccessListener { urlImagen ->
                val foto = urlImagen.toString()
                //El texto escrito
                val descripcionPublicacion = descripcion_publicacion.text.toString()
                //La fecha cuando lo escribe
                val fecha = Date()
                //El nombre del usuario que lo ha publicado que esta con la sesion activada
                val usuarioNombre = autentificacion.currentUser.displayName
                val fotoPerfil = autentificacion.currentUser.photoUrl.toString()
                val tituloPublicacion = titulo_publicacion.text.toString()
                //Cargo el objeto Publicacion con estos datos
                val publicacion = Publicacion(
                    descripcionPublicacion,
                    fecha,
                    usuarioNombre,
                    foto,
                    fotoPerfil,
                    tituloPublicacion
                )
                //Creo la colleccion para Firebase y añado mi objeto con los datos
                baseDatos.collection("Publicaciones").add(publicacion)
                    //Si esta correcto
                    .addOnSuccessListener {
                        saberSeguidores()
                        crearPublicacion_circulo2.visibility = View.INVISIBLE
                        crearPublicacion_circulo1.visibility = View.INVISIBLE
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Fallo la subida de la publicación", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun saberSeguidores() {
        val fechaFormateada = SimpleDateFormat("dd-M-yyyy hh:mm:ss")
        var fechaId = ""
        val referenciaUsuarios = baseDatos.collection("Usuarios")
        referenciaUsuarios.document(autentificacion.currentUser.displayName)
            .collection("Seguidores").get().addOnSuccessListener {
                for (seguidor in it) {
                    val notificacion = Notificacion(
                        usuarioId = autentificacion.currentUser.displayName,
                        mensaje = ", Subió una nueva publicación, venga corre a ver que novedades tiene",
                        fecha = Date()
                    )
                    fechaId = fechaFormateada.format(notificacion.fecha)
                    referenciaUsuarios.document(seguidor.id).collection("Notificaciones")
                        .document(notificacion.usuarioId.toString() + "-" + fechaId)
                        .set(notificacion).addOnSuccessListener {
                            referenciaUsuarios.document(seguidor.id)
                                .collection("Notificaciones No Leidas")
                                .document(notificacion.usuarioId.toString() + "-" + fechaId)
                                .set(notificacion)
                        }
                }
            }
    }

    //Sobreescribo el metodo para sobreescribir el valor de la Uri de la imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == file && resultCode == Activity.RESULT_OK -> {
                imagenUri = data!!.data
                foto_Publicacion.setImageURI(imagenUri)
            }
        }
    }

    //Este es el intent para que el usuario pueda escoger una foto de su galeria
    private fun seleccionarFotoGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        //Que seleccione solo imagenes de todas las extensiones
        intent.type = "image/*"
        startActivityForResult(intent, file)
    }
}