package com.example.animezone.Top

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_top.*
import kotlinx.android.synthetic.main.activity_top_ajeno.*

class TopAjenoActivity : AppCompatActivity() {
    private var baseDatos = Firebase.firestore
    private var autentificacion = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_ajeno)
        var usuarioNombre = intent.getStringExtra("Usuario_Top").toString()

        if(usuarioNombre=="null")
            usuarioNombre = autentificacion.currentUser.displayName

        listaTopsAnimes_Ajenos.layoutManager= LinearLayoutManager(this)
        listaTopsAnimes_Ajenos.adapter=TopAjenoAdapter{}
        circulo_topAjeno1.visibility=View.VISIBLE
        circulo_topAjeno2.visibility=View.VISIBLE

        baseDatos.collection("Usuarios").document(usuarioNombre).collection("TopAnime")
            .addSnapshotListener { value, error ->
                var animes= value!!.toObjects(Top::class.java)
                (listaTopsAnimes_Ajenos.adapter as TopAjenoAdapter).colocarAnime(animes)
                if(animes.size<=0)
                    notiene_top_tx.visibility=View.VISIBLE
                else
                    notiene_top_tx.visibility=View.INVISIBLE

                circulo_topAjeno1.visibility=View.INVISIBLE
                circulo_topAjeno2.visibility=View.INVISIBLE
            }
    }
}