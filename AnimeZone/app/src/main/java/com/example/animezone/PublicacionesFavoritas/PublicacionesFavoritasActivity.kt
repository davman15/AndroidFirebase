package com.example.animezone.PublicacionesFavoritas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animezone.Publicaciones.Publicacion
import com.example.animezone.Publicaciones.PublicacionAdapter
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_publicaciones_favoritas.*

class PublicacionesFavoritasActivity : AppCompatActivity() {
    private var baseDatos = Firebase.firestore
    private var autentificacion = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publicaciones_favoritas)

        baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName).collection("Favoritos")
            .addSnapshotListener { value, error ->
                var publicaciones = value!!.toObjects(Publicacion::class.java)
                //Le agrego el id del usuario quien hizo la publicacion
                publicaciones.forEachIndexed { index, publicacion ->
                    publicacion.uid = value.documents[index].id
                }
                listaPublicacionesFavoritas_rv.apply {
                    //El tamaño es fijo del recyclerView
                    setHasFixedSize(true)
                    //Lo voy a mostrar por pantalla con un LinearLayout
                    layoutManager = LinearLayoutManager(this@PublicacionesFavoritasActivity)
                    //El recyclerView nos pide un adapter que es el q hemos hecho: minuto 40 y 1h por ahi
                    adapter = PublicacionesFavoritasAdapter(
                        this@PublicacionesFavoritasActivity,
                        publicaciones
                    )
                }
            }
    }
}