package com.example.animezone.Publicaciones

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animezone.Clase.Usuario
import com.example.animezone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_listapublicaciones.*

class ListaPublicacionesActivity : AppCompatActivity() {
    //Variables de Firebase
    private val autentificacion= FirebaseAuth.getInstance()
    private val basedeDatos= Firebase.firestore

    //Array de imagenes
    var imagenes= intArrayOf(
        R.drawable.kimi,
        R.drawable.fondoanime,
        R.drawable.estrellado
    )
    //Las descripciones que van con cada foto
    var titulos= arrayOf(
        "Kimi No Nawa",
        "Tus Creaciones",
        "Wallpapers"
    )
    /*carrusel.pageCount=titulos.size
        //Coloco las imagenes en el
        carrusel.setImageListener{position, imageView ->
            imageView.setImageResource(imagenes[position])
        }
        //Si tu haces click a la imagen te da la descripcion mediante un toast
        carrusel.setImageClickListener {position ->
            Toast.makeText(applicationContext,titulos[position], Toast.LENGTH_SHORT).show()
        }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listapublicaciones)
        //Creo una coleccion de Publicaciones y convierto en objetos de tipo Publicaciones y lo ordeno desde la ultima publicacion a la primera publicada
        basedeDatos.collection("Publicaciones").orderBy("fecha", Query.Direction.DESCENDING).addSnapshotListener{ value, error ->
            var publicaciones=value!!.toObjects(Publicacion::class.java)
            //Le agrego el id del usuario quien hizo la publicacion
            publicaciones.forEachIndexed { index, publicacion ->
                publicacion.uid=value.documents[index].id
            }

            reciclerView.apply {
                //El tamaño es fijo del recyclerView
                setHasFixedSize(true)
                layoutManager= LinearLayoutManager(this@ListaPublicacionesActivity)
                adapter= PublicacionAdapter(
                    this@ListaPublicacionesActivity,
                    publicaciones
                )
            }
        }


        //Boton flotante que nos lleva a la activity donde haremos las publicaciones
        anadir.setOnClickListener{
            val anadirIntent= Intent(this,
                CrearPublicacionActivity::class.java)
            startActivity(anadirIntent)
        }
    }

}