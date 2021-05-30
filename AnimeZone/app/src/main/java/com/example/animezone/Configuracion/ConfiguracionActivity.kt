package com.example.animezone.Configuracion

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AlertDialog
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_configuracion.*

class ConfiguracionActivity : AppCompatActivity() {
    private var baseDatos = Firebase.firestore
    private var autentificacion = Firebase.auth
    private val usuarioConectado = Firebase.auth.currentUser
    private var referenciaUsuarios = baseDatos.collection("Usuarios")
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
        setContentView(R.layout.activity_configuracion)

        borrarCuenta_cv.setOnClickListener {
            val intent = Intent(this, AutentificarCredencialesActivity::class.java)
            startActivity(intent)
        }
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
            .document(autentificacion.currentUser.displayName)
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
