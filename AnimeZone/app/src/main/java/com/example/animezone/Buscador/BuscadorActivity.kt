package com.example.animezone.Buscador

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animezone.Clase.Usuario
import com.example.animezone.Perfil.PerfilAjenoActivity
import com.example.animezone.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_buscador.*


class BuscadorActivity : AppCompatActivity() {
    private var baseDatos = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscador)

        buscador_rv.layoutManager = LinearLayoutManager(this)
        buscador_rv.adapter = BuscadorAdapter { usuario ->
            seleccionarContacto(usuario)
        }

        //Se gestiona aqui el searchview
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //Aqui es si le da al icono buscar, le quita el focus
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            //Aqui se gestiona a medida q va a escribiendo el usuario y se adapta con el metodo filtrador Usuarios
            override fun onQueryTextChange(nombreBuscador: String?): Boolean {
                baseDatos.collection("Usuarios").get()
                    .addOnSuccessListener {
                        val listaUsuarios = it.toObjects(Usuario::class.java)
                        nombreBuscador?.let { nombreIntroducido ->
                            (buscador_rv.adapter as BuscadorAdapter).filtradorUsuarios(
                                listaUsuarios,
                                nombreIntroducido
                            )
                        }
                    }
                return false
            }

        })
    }

    private fun seleccionarContacto(usuario: Usuario) {
        val intent = Intent(this, PerfilAjenoActivity::class.java)
        intent.putExtra("UsuarioChat", usuario.usuarioId.toString())
        startActivity(intent)
    }
}