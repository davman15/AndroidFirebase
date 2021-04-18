package com.example.pruebafirebase

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AlertDialog
import com.example.pruebafirebase.Clase.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_registro.*

class RegistroActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val basedeDatos = Firebase.firestore
    private var usuarioExiste = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        registrarse.setOnClickListener {
            val nombre = nombre.text.toString()
            val email = email.text.toString()
            val usuarioId = nickname.text.toString()
            val contrasena = contrasena.text.toString()


            if (nombre != "" && email != "" && usuarioId != "" && contrasena != "") {
                auth.createUserWithEmailAndPassword(email, contrasena)
                    .addOnSuccessListener {
                        //Creo el objeto que voy a enviar a la base de datos
                        val usuario: Usuario = Usuario(nombre, email, usuarioId, contrasena)
                        //Creo la coleccion que va a haber en la base de datos que se va a llamar Usuarios
                        //Un documento que su id va a ser el nickname, y el objeto usuario lo meto a la base de datos
                        basedeDatos.collection("Usuarios").document(usuarioId).set(usuario)
                            //Si esta bien
                            .addOnSuccessListener {
                                //Aqui lo que hago es si esta bien, el registro, (tuve un problema que no me cogia el displayName del que esta iniciado sesion)
                                //Actualizo el perfil y le doy valor al displayName ya que me daba valor null
                                val cambiarNick = userProfileChangeRequest {
                                    displayName = usuarioId
                                }
                                auth.currentUser.updateProfile(cambiarNick)

                                AlertDialog.Builder(this).apply {
                                    setTitle("Cuenta Creada")
                                    setMessage("Tu cuenta ha sido creado correctamente")
                                    setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>")) { _: DialogInterface, _: Int ->
                                        finish()
                                    }
                                }.show()
                            }
                            .addOnFailureListener {
                                //LLamo a la funcion showError de mi clase con funciones
                                funciones.showError(this, it.message.toString())
                            }
                    }
            } else if (!usuarioExiste) {
                AlertDialog.Builder(this).apply {
                    setTitle("Usuario Duplicado")
                    setMessage("Por favor elija otro nombre de Usuario")
                    setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>")) { _: DialogInterface, _: Int ->
                        finish()
                    }
                }.show()
            } else {
                AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    setMessage("Por favor rellene todos los campos correctamente")
                    setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>"), null)
                }.show()

            }


        }
    }
}