package com.example.animezone.Publicaciones

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.animezone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_crear_publicacion.*
import java.util.*

class CrearPublicacionActivity : AppCompatActivity() {
    //Variables Firebase
    private val autentificacion = FirebaseAuth.getInstance()
    private val baseDatos = FirebaseFirestore.getInstance()

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
            //Selecciono el Uri de la imagen elegida por el usuario
            var fileUri = imagenUri
            if(fileUri==null){
                AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    setMessage("Seleccione una foto para hacer una publicación")
                    setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Si</font>")) { dialogInterface: DialogInterface, i: Int ->
                        null
                    }
                }.show()
                return@setOnClickListener
            }

            //Aqui creo la subcarpeta de la referencia
            val folder = storageReferencia.child("imagenPublicacion/${fileUri!!.lastPathSegment}")
            //Lo subimos al Storage
            folder.putFile(fileUri).addOnSuccessListener {
                //Si se sube bien al Storage, Creo el link con downloadURL
                folder.downloadUrl.addOnSuccessListener { urlImagen ->
                    val foto=urlImagen.toString()
                    //El texto escrito
                    val textoPublicacion = descripcion_publicacion.text.toString()
                    //La fecha cuando lo escribe
                    val fecha = Date()
                    //El nombre del usuario que lo ha publicado que esta con la sesion activada
                    val usuarioNombre = autentificacion.currentUser.displayName
                    val fotoPerfil=autentificacion.currentUser.photoUrl.toString()
                    //Cargo el objeto Publicacion con estos datos
                    val publicacion =
                        Publicacion(
                            textoPublicacion,
                            fecha,
                            usuarioNombre,
                            foto,
                            fotoPerfil
                        )
                    //Creo la colleccion para Firebase y añado mi objeto con los datos
                    baseDatos.collection("Publicaciones").add(publicacion)
                        //Si esta correcto
                        .addOnSuccessListener {
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                        }
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