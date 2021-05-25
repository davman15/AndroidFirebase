package com.example.animezone.TusPublicaciones

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animezone.Publicaciones.Publicacion
import com.example.animezone.PublicacionesFavoritas.PublicacionesFavoritasAdapter
import com.example.animezone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_tus_publicaciones.*

class TusPublicacionesActivity : AppCompatActivity() {

    private val autentificacion = FirebaseAuth.getInstance()
    private val basedeDatos = Firebase.firestore
    private val lista = basedeDatos.collection("Publicaciones").whereEqualTo("usuarioNombre",autentificacion.currentUser.displayName)
        .orderBy("fecha", Query.Direction.DESCENDING)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tus_publicaciones)
        tuspublicaciones_circulo1.visibility=View.VISIBLE
        tuspublicaciones_circulo2.visibility=View.VISIBLE
        lista.addSnapshotListener { value, error ->
            val publicaciones = value!!.toObjects(Publicacion::class.java)

            //Le agrego el id del usuario quien hizo la publicacion
            publicaciones.forEachIndexed { index, publicacion ->
                publicacion.uid = value.documents[index].id
            }

            tuspublicaciones_rv.apply {
                //El tamaño es fijo del recyclerView
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@TusPublicacionesActivity)
                adapter = TusPublicacionesAdapter(
                    this@TusPublicacionesActivity,
                    publicaciones
                )
            }
            tuspublicaciones_circulo1.visibility=View.INVISIBLE
            tuspublicaciones_circulo2.visibility=View.INVISIBLE
        }

    }
}