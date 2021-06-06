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

class TopActivity : AppCompatActivity() {
    private var baseDatos = Firebase.firestore
    private var autentificacion = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)

        listaTopsAnimes.layoutManager= LinearLayoutManager(this)
        listaTopsAnimes.adapter=TopAdapter{}
        circulotop1.visibility= View.VISIBLE
        circulotop2.visibility=View.VISIBLE

        baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName).collection("TopAnime")
            .addSnapshotListener { value, error ->
                var animes= value!!.toObjects(Top::class.java)
                (listaTopsAnimes.adapter as TopAdapter).colocarAnime(animes)

                if(animes.size<=0)
                    notienesanimes_tx.visibility=View.VISIBLE
                else
                    notienesanimes_tx.visibility=View.INVISIBLE

                circulotop1.visibility= View.INVISIBLE
                circulotop2.visibility=View.INVISIBLE
            }
    }
}