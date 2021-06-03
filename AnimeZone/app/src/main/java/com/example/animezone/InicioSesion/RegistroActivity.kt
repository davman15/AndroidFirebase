package com.example.animezone.InicioSesion

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.TextUtils.isEmpty
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.animezone.Clase.Usuario
import com.example.animezone.MenuPrincipalActivity
import com.example.animezone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_registro.*
import java.math.BigInteger
import java.security.MessageDigest

class RegistroActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val basedeDatos = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        registrarse.setOnClickListener {
            registro_circulo1.visibility= View.VISIBLE
            registro_circulo2.visibility= View.VISIBLE
            val nombre = nombre_texto.text.toString().trim()
            val email = email_texto.text.toString().trim()
            val usuarioId = nickname_texto.text.toString().trim()
            val contrasena = contrasena_texto.text.toString().trim()
            //Como un switch
            if (validarCamposRegistro(nombre, usuarioId, email, contrasena))
                return@setOnClickListener

            basedeDatos.collection("Usuarios").document(usuarioId).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        registro_circulo1.visibility= View.INVISIBLE
                        registro_circulo2.visibility= View.INVISIBLE
                        AlertDialog.Builder(this).apply {
                            setTitle("Nickname en uso")
                            setMessage("Escoja un nickname más original y único")
                            setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>")) { _: DialogInterface, _: Int ->
                            }
                        }.show()
                        nickname_texto.requestFocus()
                        nickname_texto.setError("Inserte otro nickname")
                    } else {
                        //Si todos los campos son rellenados correctamente
                        validarCredencialesRegistro(email, contrasena, nombre, usuarioId)
                    }
                }
        }
    }

    private fun validarCamposRegistro(
        nombre: String,
        usuarioId: String,
        email: String,
        contrasena: String
    ): Boolean {
        when {
            isEmpty(nombre) -> {
                nombre_texto.setError("Introduzca su nombre")
                nombre_texto.requestFocus()
                registro_circulo1.visibility = View.INVISIBLE
                registro_circulo2.visibility = View.INVISIBLE
                return true
            }
            isEmpty(usuarioId) -> {
                nickname_texto.setError("Introduzca su nombre de usuario")
                nickname_texto.requestFocus()
                registro_circulo1.visibility = View.INVISIBLE
                registro_circulo2.visibility = View.INVISIBLE
                return true
            }
            isEmpty(email) -> {
                email_texto.setError("Introduzca su email")
                email_texto.requestFocus()
                registro_circulo1.visibility = View.INVISIBLE
                registro_circulo2.visibility = View.INVISIBLE
                return true
            }
            isEmpty(contrasena) -> {
                contrasena_texto.setError("Introduzca su contraseña")
                contrasena_texto.requestFocus()
                registro_circulo1.visibility = View.INVISIBLE
                registro_circulo2.visibility = View.INVISIBLE
                return true
            }
        }
        return false
    }

    //Si se repite el correo en la base de datos o si hay algun otro tipo de error
    private fun validarCredencialesRegistro(
        email: String,
        contrasena: String,
        nombre: String,
        usuarioId: String
    ) {
        auth.createUserWithEmailAndPassword(email, contrasena)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    registro_circulo1.visibility = View.INVISIBLE
                    registro_circulo2.visibility = View.INVISIBLE
                    AlertDialog.Builder(this).apply {
                        setTitle("Registro Fallido")
                        setMessage("Por favor, inténtalo de nuevo")
                        setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>")) { _: DialogInterface, _: Int ->
                        }
                    }.show()
                }
            }
            .addOnSuccessListener {
                var contrasenaEncriptada=md5(contrasena)
                println(contrasenaEncriptada)
                registrarUsuario(nombre, email, usuarioId, contrasenaEncriptada)

            }
    }

    private fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    private fun registrarUsuario(
        nombre: String,
        email: String,
        usuarioId: String,
        contrasena: String
    ) {
        val foto = "https://firebasestorage.googleapis.com/v0/b/animezone-82466.appspot.com/o/ImagenPerfilPorDefecto%2Fsinperfil.png?" +
                "alt=media&token=79062551-4c24-45d7-9243-21030e6755b9"
        val usuario = Usuario(nombre, null, email, usuarioId, contrasena, foto, "Me encanta el anime. espero que nos llevemos bien!! xD")

        basedeDatos.collection("Usuarios").document(usuarioId).set(usuario)

        val cambiarNick = userProfileChangeRequest {
            displayName = usuarioId
            photoUri = Uri.parse(foto)
        }
        registro_circulo1.visibility = View.INVISIBLE
        registro_circulo2.visibility = View.INVISIBLE
        auth.currentUser.updateProfile(cambiarNick)
        AlertDialog.Builder(this).apply {
            setTitle("Cuenta Creada")
            setMessage("Se ha registrado correctamente")
            setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>")) { _: DialogInterface, _: Int ->
                //Aqui directamente le digo que no se borre lo anterior y que vaya al menu directamente
                finishAffinity()
                val intent = Intent(applicationContext, MenuPrincipalActivity::class.java)
                startActivity(intent)
            }.show()
        }
    }
}
