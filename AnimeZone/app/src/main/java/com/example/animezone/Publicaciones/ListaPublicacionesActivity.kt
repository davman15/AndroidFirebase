package com.example.animezone.Publicaciones

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animezone.R
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_listapublicaciones.*

class ListaPublicacionesActivity : AppCompatActivity() {

    private val basedeDatos = Firebase.firestore
    private val lista = basedeDatos.collection("Publicaciones")
        .orderBy("fecha", Query.Direction.DESCENDING)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listapublicaciones)
        listaPublicaciones_circulo2.visibility= View.VISIBLE
        listaPublicaciones_circulo1.visibility= View.VISIBLE
        //Creo una coleccion de Publicaciones y convierto en objetos de tipo Publicaciones y
        // lo ordeno desde la ultima publicacion a la primera publicada

        lista.get().addOnSuccessListener {
            val publicaciones = it!!.toObjects(Publicacion::class.java)
            //Le agrego el id del usuario quien hizo la publicacion
            publicaciones.forEachIndexed { index, publicacion ->
                publicacion.uid = it.documents[index].id
            }

            reciclerView.apply {
                //El tamaño es fijo del recyclerView
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@ListaPublicacionesActivity)
                adapter = PublicacionAdapter(this@ListaPublicacionesActivity, publicaciones)
            }
            listaPublicaciones_circulo2.visibility= View.INVISIBLE
            listaPublicaciones_circulo1.visibility= View.INVISIBLE
        }

        //Boton flotante que nos lleva a la activity donde haremos las publicaciones
        anadir.setOnClickListener {
            val anadirIntent = Intent(
                this,
                CrearPublicacionActivity::class.java
            )
            startActivity(anadirIntent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        lista.get().addOnSuccessListener {
            val publicaciones = it!!.toObjects(Publicacion::class.java)
            //Le agrego el id del usuario quien hizo la publicacion
            publicaciones.forEachIndexed { index, publicacion ->
                publicacion.uid = it.documents[index].id
            }
            reciclerView.apply {
                adapter = PublicacionAdapter(this@ListaPublicacionesActivity,publicaciones)
            }
        }
    }
}