package com.example.animezone.InicioSesion

import android.content.DialogInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.TextUtils.isEmpty
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.animezone.Clase.Usuario
import com.example.animezone.Publicaciones.Publicacion
import com.example.animezone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_registro.*

class RegistroActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val basedeDatos = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        registrarse.setOnClickListener {
            val nombre = nombre_texto.text.toString().trim()
            val email = email_texto.text.toString().trim()
            val usuarioId = nickname_texto.text.toString().trim()
            val contrasena = contrasena_texto.text.toString().trim()
            //Como un switch
            when {
                isEmpty(nombre) -> {
                    nombre_texto.setError("Introduzca su nombre")
                    nombre_texto.requestFocus()
                    return@setOnClickListener
                }
                isEmpty(usuarioId) -> {
                    nickname_texto.setError("Introduzca su nombre de usuario")
                    nickname_texto.requestFocus()
                    return@setOnClickListener
                }
                isEmpty(email) -> {
                    email_texto.setError("Introduzca su email")
                    email_texto.requestFocus()
                    return@setOnClickListener
                }
                isEmpty(contrasena) -> {
                    contrasena_texto.setError("Introduzca su contraseña")
                    contrasena_texto.requestFocus()
                    return@setOnClickListener
                }
            }
            basedeDatos.collection("Usuarios").document(usuarioId).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        AlertDialog.Builder(this).apply {
                            setTitle("Nickname en uso")
                            setMessage("Escoja un nickname más original y único")
                            setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>")) { _: DialogInterface, _: Int ->
                            }
                        }.show()
                        nickname_texto.requestFocus()
                        nickname_texto.setError("Inserte otro nickname")
                    } else {
                        //Toast.makeText(applicationContext, "Valor -> $resultado", Toast.LENGTH_SHORT).show()
                        //Si todos los campos son rellenados correctamente
                        auth.createUserWithEmailAndPassword(email, contrasena)
                            .addOnCompleteListener {
                                if (!it.isSuccessful) {
                                    AlertDialog.Builder(this).apply {
                                        setTitle("Registro Fallido")
                                        setMessage("Por favor, inténtalo de nuevo")
                                        setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>")) { _: DialogInterface, _: Int ->
                                        }
                                    }.show()
                                }
                            }
                            .addOnSuccessListener {
                                //Meto la foto directamente en la variable
                                val foto =
                                    "https://firebasestorage.googleapis.com/v0/b/animezone-82466.appspot.com/o/ImagenPerfilPorDefecto%2Fsinperfil.png?alt=media&token=79062551-4c24-45d7-9243-21030e6755b9"
                                //Creo el objeto que voy a enviar a la base de datos
                                val usuario: Usuario =
                                    Usuario(nombre, null, email, usuarioId, contrasena, foto)
                                //Creo la coleccion que va a haber en la base de datos que se va a llamar usuarios , un documento que su id va a ser el nickname, y el objeto usuario lo meto a la base de datos
                                basedeDatos.collection("Usuarios").document(usuarioId).set(usuario)
                                //Aqui lo que hago es si esta bien, el registro, (tuve un problema que no me cogia el displayName del que esta iniciado sesion)
                                //Actualizo el perfil y le doy valor al displayName ya que me daba valor null
                                val cambiarNick = userProfileChangeRequest {
                                    displayName = usuarioId
                                    photoUri = Uri.parse(foto)
                                }
                                auth.currentUser.updateProfile(cambiarNick)
                                AlertDialog.Builder(this).apply {
                                    setTitle("Cuenta Creada")
                                    setMessage("Se ha registrado correctamente")
                                    setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>")) { _: DialogInterface, _: Int ->
                                        finish()
                                    }.show()
                                }
                            }
                    }
                }
        }
    }
}
