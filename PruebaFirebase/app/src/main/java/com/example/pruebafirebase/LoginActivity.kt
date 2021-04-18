package com.example.pruebafirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registro.*

class LoginActivity : AppCompatActivity() {
    private val autentificacion= FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Verificar si el usuario q va a entrar de nuevo a la aplicacion ya habia iniciado sesion anteriormente
        if(autentificacion.currentUser!=null){
            iniciarSesion()
        }

        //Si quiere iniciar Sesion
        login_btn.setOnClickListener{
            val email=correo.text.toString()
            val password=password.text.toString()
            if(email!="" && password!="") {
                autentificacion.signInWithEmailAndPassword(email, password)
                    //Si va bien lo mandare al activity de entrada a la aplicacion
                    .addOnSuccessListener {
                        iniciarSesion()
                    }
                    .addOnFailureListener {
                        //LLamo a la funcion showError de mi clase con funciones
                        funciones.showError(this, it.message.toString())
                    }
            }
            else{
                AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    setMessage("Los campos no están correctamente")
                    setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>"),null)
                }.show()
            }
        }

        //Si quiere Registrarse
        registro.setOnClickListener{
            val intent=Intent(this,RegistroActivity::class.java)
            startActivity(intent)
        }
    }
    private fun iniciarSesion(){
        val intent = Intent(this, EntradaActivity::class.java)
        startActivity(intent)
        //Esto hace que si el usuario quiere ir atras no pueda volver a esta activity
        finish()
    }
}