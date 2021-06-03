package com.example.animezone.PublicacionesFavoritas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animezone.Publicaciones.Publicacion
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_publicaciones_favoritas.*

class PublicacionesFavoritasActivity : AppCompatActivity() {
    private var baseDatos = Firebase.firestore
    private var autentificacion = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publicaciones_favoritas)
        listaPublicacionesFavoritas_circulo1.visibility= View.VISIBLE
        listaPublicacionesFavoritas_circulo2.visibility= View.VISIBLE

        baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName).collection("Favoritos")
            .addSnapshotListener { value, error ->
                var publicaciones = value!!.toObjects(Publicacion::class.java)
                publicaciones.forEachIndexed { index, publicacion ->
                    publicacion.uid = value.documents[index].id
                }
                if(publicaciones.size<=0)
                    notienesfavoritos_tv.visibility=View.VISIBLE
                else
                    notienesfavoritos_tv.visibility=View.INVISIBLE

                listaPublicacionesFavoritas_rv.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(this@PublicacionesFavoritasActivity)
                    adapter = PublicacionesFavoritasAdapter(this@PublicacionesFavoritasActivity,
                        publicaciones)

                    listaPublicacionesFavoritas_circulo1.visibility= View.INVISIBLE
                    listaPublicacionesFavoritas_circulo2.visibility= View.INVISIBLE
                }
            }
    }
}