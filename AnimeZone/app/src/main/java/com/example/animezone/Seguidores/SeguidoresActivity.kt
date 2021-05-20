package com.example.animezone.Seguidores

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animezone.Clase.Usuario
import com.example.animezone.Perfil.PerfilAjenoActivity
import com.example.animezone.ProgressBar.CargandoDialog
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_seguidores.*


class SeguidoresActivity : AppCompatActivity() {
    private var baseDatos = Firebase.firestore
    private var autentificacion = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguidores)
        //Enseñar ProgressBar
        val cargando = CargandoDialog(this)
        cargando.empezarCarga()
        val handler = Handler()
        handler.postDelayed({ cargando.cancelable() }, 1900)
        listaSeguidores.layoutManager = LinearLayoutManager(this)

        listaSeguidores.adapter = SeguidoresAdapter { usuario ->
            seleccionarSeguidor(usuario)
        }


        baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName)
            .collection("Seguidores")
            .addSnapshotListener { value, error ->
                val listaUsuarios = value?.toObjects(Usuario::class.java)
                if (listaUsuarios != null) {
                    (listaSeguidores.adapter as SeguidoresAdapter).listaActualizada(listaUsuarios)
                }
            }
    }

    private fun seleccionarSeguidor(usuario: Usuario) {
        val intent = Intent(this, PerfilAjenoActivity::class.java)
        intent.putExtra("UsuarioInfo", usuario.usuarioId.toString())
        startActivity(intent)
    }
}