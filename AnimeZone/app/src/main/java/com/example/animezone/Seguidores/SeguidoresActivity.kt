package com.example.animezone.Seguidores

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animezone.Clase.Usuario
import com.example.animezone.Perfil.PerfilAjenoActivity
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_seguidores.*


class SeguidoresActivity : AppCompatActivity() {
    private var baseDatos = Firebase.firestore
    private var autentificacion = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguidores)
        listaSeguidores_circulo2.visibility= View.VISIBLE
        listaSeguidores_circulo1.visibility= View.VISIBLE

        listaSeguidores.layoutManager = LinearLayoutManager(this)
        listaSeguidores.adapter = SeguidoresAdapter { usuario ->
            seleccionarSeguidor(usuario)
        }

        baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName)
            .collection("Seguidores").orderBy("usuarioId")
            .addSnapshotListener { value, error ->
                val listaUsuarios = value?.toObjects(Usuario::class.java)
                if (listaUsuarios != null) {
                  (listaSeguidores.adapter as SeguidoresAdapter).listaActualizada(listaUsuarios)
                    listaSeguidores_circulo2.visibility= View.INVISIBLE
                    listaSeguidores_circulo1.visibility= View.INVISIBLE
                }
            }
    }

    private fun seleccionarSeguidor(usuario: Usuario) {
        val intent = Intent(this, PerfilAjenoActivity::class.java)
        intent.putExtra("UsuarioChat", usuario.usuarioId.toString())
        startActivity(intent)
    }
}