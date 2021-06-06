package com.example.animezone.InicioSesion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_contrasena_olvidada.*


class ContrasenaOlvidadaActivity : AppCompatActivity() {
    private val autentificacion = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contrasena_olvidada)
        recuperar_btn.setOnClickListener {
            if (correo_recuperar_tx.text.isEmpty()) {
                correo_recuperar_tx.setError("Introduzca su email")
                correo_recuperar_tx.requestFocus()
                return@setOnClickListener
            }
            enviarCorreo()
        }

    }

    private fun enviarCorreo() {
        circulo_recuperar1.visibility=View.VISIBLE
        circulo_recuperar2.visibility=View.VISIBLE
        autentificacion.languageCode = "es"
        autentificacion.sendPasswordResetEmail(correo_recuperar_tx.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    circulo_recuperar1.visibility=View.INVISIBLE
                    circulo_recuperar2.visibility=View.INVISIBLE
                    Toast.makeText(this, "Por favor revise su correo, para restablecer su contraseña", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else{
                    circulo_recuperar1.visibility=View.INVISIBLE
                    circulo_recuperar2.visibility=View.INVISIBLE
                    Toast.makeText(this, "Por favor introduzca un correo existente", Toast.LENGTH_LONG).show()
                }
            }
    }
}