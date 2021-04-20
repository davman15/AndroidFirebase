package com.example.animezone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.actionCodeSettings
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val autentificacion = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Verificar si el usuario q va a entrar de nuevo a la aplicacion ya habia iniciado sesion anteriormente
        if (autentificacion.currentUser != null) {
            iniciarSesion()
        }

        //Si quiere iniciar Sesion
        login_btn.setOnClickListener {
            val email = correo_texto1.text.toString().trim()
            val password = contrasena_texto1.text.toString().trim()

            if (email.isEmpty()) {
                correo_texto1.setError("Introduzca su email")
                correo_texto1.requestFocus()
                return@setOnClickListener
            } else if (password.isEmpty()) {
                contrasena_texto1.setError("Introduzca su contraseña")
                contrasena_texto1.requestFocus()
                return@setOnClickListener
            }

            autentificacion.signInWithEmailAndPassword(email, password)
                //Si va bien lo mandare al activity de entrada a la aplicacion
                .addOnSuccessListener {
                    iniciarSesion()
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this).apply {
                        setTitle("Inicio Erróneo")
                        setMessage("Los campos no están correctamente")
                        setPositiveButton(
                            Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>"),
                            null
                        )
                    }.show()
                }
        }

        //Si quiere Registrarse
        registro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun iniciarSesion() {
        val intent = Intent(this, MenuPrincipalActivity::class.java)
        startActivity(intent)
        //Esto hace que si el usuario quiere ir atras no pueda volver a esta activity
        finish()
    }

}