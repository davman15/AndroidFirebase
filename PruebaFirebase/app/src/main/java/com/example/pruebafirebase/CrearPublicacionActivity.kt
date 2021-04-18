package com.example.pruebafirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.grpc.okhttp.internal.Util
import kotlinx.android.synthetic.main.activity_crear_publicacion.*
import java.util.*

class CrearPublicacionActivity : AppCompatActivity() {
    //Variables Firebase
    private val autentificacion=FirebaseAuth.getInstance()
    private val baseDatos= FirebaseFirestore.getInstance()
    private val storage=Firebase.storage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_publicacion)
        //Al darle publicar selecciono
        publicar_btn.setOnClickListener {
            //El texto escrito
            val textoPublicacion=descripcion_publicacion.text.toString()
            //La fecha cuando lo escribe
            val fecha=Date()
            //El nombre del usuario que lo ha publicado que esta con la sesion activada
            val usuarioNombre=autentificacion.currentUser.displayName
            //Cargo el objeto Publicacion con estos datos
            val publicacion=Publicacion(textoPublicacion,fecha,usuarioNombre)

            //Creo la colleccion para Firebase y añado mi objeto con los datos
            baseDatos.collection("Publicaciones").add(publicacion)
                //Si esta correcto
                .addOnSuccessListener {
                        finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()
                }
        }
    }
}