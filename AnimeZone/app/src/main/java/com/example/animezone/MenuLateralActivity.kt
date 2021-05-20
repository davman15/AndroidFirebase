package com.example.animezone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.animezone.Perfil.PerfilActivity
import com.example.animezone.Publicaciones.CrearPublicacionActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_menu_lateral.*
import kotlinx.android.synthetic.main.activity_perfil.*

class MenuLateralActivity : AppCompatActivity() {
    private val autentificacion = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_lateral)
    }

}