package com.example.animezone.Configuracion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.animezone.R
import kotlinx.android.synthetic.main.activity_configuracion.*

class ConfiguracionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)

        borrarCuenta_cv.setOnClickListener {
            val intent = Intent(this, AutentificarCredencialesActivity::class.java)
            startActivity(intent)
        }

        cambiarContrasena.setOnClickListener {
            val intent = Intent(this, CambiarContrasenaActivity::class.java)
            startActivity(intent)
        }

    }
}
