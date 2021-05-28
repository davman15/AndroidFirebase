package com.example.animezone.Configuracion

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AlertDialog
import com.example.animezone.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_autentificar_credenciales.*

class AutentificarCredencialesActivity : AppCompatActivity() {
    private var baseDatos = Firebase.firestore
    private var autentificacion = Firebase.auth
    private val usuarioConectado = Firebase.auth.currentUser
    private var referenciaUsuarios = baseDatos.collection("Usuarios")
    private var saberUsuarioConectado=""
    private var referenciaNotificacionesNoLeidas =
        baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName)
            .collection("Notificaciones No Leidas")

    private var referenciaNotificaciones =
        baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName)
            .collection("Notificaciones")

    private var referenciaFavoritos =
        baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName)
            .collection("Favoritos")

    private var referenciaSeguidores =
        baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName)
            .collection("Seguidores")

    private var referenciaChats =
        baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName)
            .collection("chats")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autentificar_credenciales)
        informacion_Alerta()
        saberUsuarioConectado=autentificacion.currentUser.displayName
        validar_credenciales_btn.setOnClickListener {
            if (validar_email_text.text.toString().trim() == "") {
                validar_email_text.setError("Introduzca su email")
                validar_email_text.requestFocus()
                return@setOnClickListener
            } else if (validar_contrasena_text.text.toString() == "") {
                validar_contrasena_text.setError("Introduzca su contraseña")
                validar_contrasena_text.requestFocus()
                return@setOnClickListener
            }
            val credenciales = EmailAuthProvider.getCredential(
                validar_email_text.text.toString(),
                validar_contrasena_text.text.toString()
            )

            usuarioConectado.reauthenticate(credenciales).addOnSuccessListener {
                elegirBorrarCuenta()
            }.addOnFailureListener {
                mostrarErrorCredenciales()
            }

        }
    }

    private fun informacion_Alerta() {
        AlertDialog.Builder(this).apply {
            setTitle("Eliminar Cuenta")
            setMessage("Para poder borrar su cuenta debe de volver a autentificar sus datos")
            setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>"), null)
        }.show()
    }

    private fun mostrarErrorCredenciales() {
        AlertDialog.Builder(this).apply {
            setTitle("Credenciales Incorrectas")
            setMessage("Ponga sus credenciales correctamente")
            setPositiveButton(
                Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>"),
                null
            )
        }.show()
    }



    private fun elegirBorrarCuenta() {
        AlertDialog.Builder(this).apply {
            setTitle("Eliminar Cuenta")
            setMessage("¿Estás seguro que quiere eliminar su cuenta?")
            setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Si</font>")) { dialogInterface: DialogInterface, i: Int ->
                eliminarNotificacionesNoLeidas()
            }
            setNegativeButton(Html.fromHtml("<font color='#FFFFFF'>No</font>"), null)
        }.show()
    }


    private fun eliminarNotificacionesNoLeidas() {
        referenciaNotificacionesNoLeidas.get().addOnSuccessListener { notificacionesNoLeidas ->
            if (notificacionesNoLeidas.isEmpty) {
                eliminarNotificaciones()
            } else {
                for (notificacion in notificacionesNoLeidas) {
                    referenciaNotificacionesNoLeidas.document(notificacion.id).delete()
                        .addOnCompleteListener {
                            eliminarNotificaciones()
                        }
                }
            }
        }
    }

    private fun eliminarNotificaciones() {
        referenciaNotificaciones.get().addOnSuccessListener {
            if (it.isEmpty) {
                eliminarSeguidores()
            } else {
                for (notificacion in it) {
                    referenciaNotificaciones.document(notificacion.id).delete()
                        .addOnCompleteListener {
                            eliminarSeguidores()
                        }
                }
            }
        }
    }

    private fun eliminarSeguidores() {
        referenciaSeguidores.get().addOnSuccessListener { seguidores ->
            if (seguidores.isEmpty) {
                eliminarChats()
            } else {
                for (seguidor in seguidores) {
                    referenciaSeguidores.document(seguidor.id).delete().addOnCompleteListener {
                        eliminarChats()
                    }
                }
            }
        }
    }

    private fun eliminarChats() {
        referenciaChats.get().addOnSuccessListener {
            if (it.isEmpty) {
                eliminarFavoritos()
            } else {
                for (chats in it) {
                    referenciaChats.document(chats.id).delete().addOnCompleteListener {
                        eliminarFavoritos()
                    }
                }
            }
        }
    }

    private fun eliminarFavoritos() {
        referenciaFavoritos.get().addOnSuccessListener { favorito ->
            if (favorito.isEmpty) {
                eliminarListaSeguidoresAjeno()
            }
            for (fav in favorito) {
                referenciaFavoritos.document(fav.id).delete().addOnCompleteListener {
                    eliminarListaSeguidoresAjeno()
                }
            }
        }
    }

    private fun eliminarListaSeguidoresAjeno() {
        referenciaUsuarios.get().addOnSuccessListener { usuarios ->
            for (usuario in usuarios) {
                referenciaUsuarios.document(usuario.id).collection("Seguidores").get()
                    .addOnSuccessListener { seguidores ->
                        println("entro")
                        if (seguidores.isEmpty) {
                            println("Entro en favoritos ajeno no existe")
                            borrarUsuario()
                        } else {
                            for (seguidor in seguidores) {
                                referenciaUsuarios.document(usuario.id).collection("Seguidores")
                                    .document(autentificacion.currentUser.displayName).delete()
                                    .addOnCompleteListener {
                                        borrarUsuario()
                                    }
                            }
                        }
                    }
            }
        }

    }

    private fun borrarUsuario() {
        referenciaUsuarios
            .document(saberUsuarioConectado)
            .delete()
            .addOnSuccessListener {
                println("Entro en usuarios que existe")
                eliminarPublicacionesYCredenciales()
            }
    }

    private fun eliminarPublicacionesYCredenciales() {
        baseDatos.collection("Publicaciones")
            .whereEqualTo("usuarioNombre", autentificacion.currentUser.displayName).get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    println("entro en credenciales")
                    usuarioConectado.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                finish()
                            }
                        }
                } else {
                    for (documento in it) {
                        baseDatos.collection("Publicaciones")
                            .document(documento.id).delete().addOnCompleteListener {
                                usuarioConectado.delete()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            finish()
                                        }
                                    }
                            }
                    }
                }
            }
    }
}