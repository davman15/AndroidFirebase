package com.example.animezone.Top

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animezone.Clase.Usuario
import com.example.animezone.Perfil.PerfilAjenoActivity
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_top.*
import kotlinx.android.synthetic.main.activity_top_seguidos.*

class TopSeguidosActivity : AppCompatActivity() {
    private var baseDatos = Firebase.firestore
    private var autentificacion = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_seguidos)

        listaSeguidos.layoutManager= LinearLayoutManager(this)
        listaSeguidos.adapter=TopSeguidosAdapter{seguido->
            seleccionarSeguido(seguido)
        }

        circulo_seguidos1.visibility= View.VISIBLE
        circulo_seguidos2.visibility=View.VISIBLE

        baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName).collection("Seguidos")
            .addSnapshotListener { value, error ->
                var seguidos=value!!.toObjects(Usuario::class.java)
                (listaSeguidos.adapter as TopSeguidosAdapter).listaActualizada(seguidos)
                if(seguidos.size<=0)
                    notienesseguidos_tx.visibility=View.VISIBLE
                else
                    notienesseguidos_tx.visibility=View.INVISIBLE

                circulo_seguidos1.visibility= View.INVISIBLE
                circulo_seguidos2.visibility=View.INVISIBLE
        }
    }

    private fun seleccionarSeguido(usuario: Usuario) {
        val intent = Intent(this, PerfilAjenoActivity::class.java)
        intent.putExtra("UsuarioChat", usuario.usuarioId.toString())
        startActivity(intent)
    }
}