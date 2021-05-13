package com.example.animezone.Configuracion

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AlertDialog
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_configuracion.*

class ConfiguracionActivity : AppCompatActivity() {
    private var baseDatos = Firebase.firestore
    private var autentificacion = Firebase.auth
    private val usuarioConectado = Firebase.auth.currentUser
    private var usuarioIdo = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)
        borrarCuenta_cv.setOnClickListener {
            elegirBorrarCuenta()
        }
    }

    private fun elegirBorrarCuenta() {
        AlertDialog.Builder(this).apply {
            setTitle("Eliminar Cuenta")
            setMessage("¿Estás seguro que quiere eliminar su cuenta?")
            setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Si</font>")) { dialogInterface: DialogInterface, i: Int ->
                baseDatos.collection("Usuarios")
                    .document(autentificacion.currentUser.displayName)
                    .delete()
                    .addOnSuccessListener {
                        usuarioIdo = autentificacion.currentUser.displayName
                        baseDatos.collection("Usuarios")
                            .document(autentificacion.currentUser.displayName)
                            .delete()
                            .addOnSuccessListener {
                                usuarioConectado.delete()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            baseDatos.collection("Publicaciones")
                                                .whereEqualTo("usuarioNombre", usuarioIdo).get()
                                                .addOnSuccessListener {
                                                    for (documento in it) {
                                                        baseDatos.collection("Publicaciones")
                                                            .document(documento.id).delete()
                                                    }
                                                }
                                            finish()
                                        }
                                    }
                            }
                    }
            }
            setNegativeButton(Html.fromHtml("<font color='#FFFFFF'>No</font>"), null)
        }.show()
    }
}
