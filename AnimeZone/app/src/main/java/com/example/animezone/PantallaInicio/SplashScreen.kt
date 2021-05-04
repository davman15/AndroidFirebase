package com.example.animezone.PantallaInicio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.animezone.InicioSesion.LoginActivity
import com.example.animezone.MenuPrincipalActivity
import com.example.animezone.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_splash_screen.*


class SplashScreen : AppCompatActivity() {
    private val autentificacion = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        pantalla_carga.alpha = 0f
        if (autentificacion.currentUser == null) {
            pantalla_carga.animate().setDuration(1500).alpha(1f).withEndAction {
                val paginaPrincipal = Intent(
                    this,
                    LoginActivity::class.java
                )
                startActivity(paginaPrincipal)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        } else {
            pantalla_carga.animate().setDuration(100).alpha(1f).withEndAction {
                val paginaPrincipal = Intent(
                    this,
                    MenuPrincipalActivity::class.java
                )
                startActivity(paginaPrincipal)
                finish()
            }
        }
    }


}