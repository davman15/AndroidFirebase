package com.example.animezone.Configuracion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_cambiar_contrasena.*

class CambiarContrasenaActivity : AppCompatActivity() {
    private val autentificacion = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambiar_contrasena)
        validar_correo_btn.setOnClickListener {
            if (pedir_correo_tx.text.toString()!=autentificacion.currentUser.email){
                pedir_correo_tx.setError("Introduzca su email correspondiente")
                pedir_correo_tx.requestFocus()
                return@setOnClickListener
            }
            enviarCorreo()
        }
    }

    private fun enviarCorreo() {
        circulo_cambiar1.visibility= View.VISIBLE
        circulo_cambiar2.visibility=View.VISIBLE
        autentificacion.languageCode = "es"
        autentificacion.sendPasswordResetEmail(pedir_correo_tx.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful){
                    circulo_cambiar1.visibility= View.INVISIBLE
                    circulo_cambiar2.visibility=View.INVISIBLE
                    Toast.makeText(this, "Por favor revise su correo, para cambiar su contraseña", Toast.LENGTH_LONG).show()
                }
                 else {
                    circulo_cambiar1.visibility = View.INVISIBLE
                    circulo_cambiar2.visibility = View.INVISIBLE
                    Toast.makeText(this, "Por favor introduzca un correo existente", Toast.LENGTH_LONG).show()
                }
            }
    }
}