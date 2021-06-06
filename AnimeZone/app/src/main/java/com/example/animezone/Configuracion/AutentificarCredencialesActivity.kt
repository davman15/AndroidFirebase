package com.example.animezone.Configuracion

import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.animezone.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_autentificar_credenciales.*

class AutentificarCredencialesActivity : AppCompatActivity() {
    private var baseDatos = Firebase.firestore
    private var autentificacion = Firebase.auth
    private val usuarioConectado = Firebase.auth.currentUser
    private var referenciaUsuarios = baseDatos.collection("Usuarios")
    private var saberUsuarioConectado = ""
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

    private var referenciaTopAnime=baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName).collection("TopAnime")
    private var referenciaSeguidos=baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName).collection("Seguidos")
    private var referenciaPublicaciones = baseDatos.collection("Publicaciones")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autentificar_credenciales)
        informacion_Alerta()
        saberUsuarioConectado = autentificacion.currentUser.displayName
        validar_credenciales_btn.setOnClickListener {
            if (validar_email_tx.text.toString().trim() == "") {
                validar_email_tx.setError("Introduzca su email")
                validar_email_tx.requestFocus()
                return@setOnClickListener
            } else if (validar_contrasena_text.text.toString() == "") {
                validar_contrasena_text.setError("Introduzca su contraseña")
                validar_contrasena_text.requestFocus()
                return@setOnClickListener
            }
            val credenciales = EmailAuthProvider.getCredential(
                validar_email_tx.text.toString(),
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
        circulo1_credenciales1.visibility = View.VISIBLE
        circulo1_credenciales2.visibility = View.VISIBLE
        referenciaNotificacionesNoLeidas.get().addOnSuccessListener { notificacionesNoLeidas ->
            if (notificacionesNoLeidas.isEmpty)
                eliminarNotificaciones()
            else {
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
                eliminarSeguidos()
            }
            for (fav in favorito) {
                referenciaFavoritos.document(fav.id).delete().addOnCompleteListener {
                    eliminarSeguidos()
                }
            }
        }
    }

    private fun eliminarSeguidos(){
        referenciaSeguidos.get().addOnSuccessListener { seguidos ->
            if(seguidos.isEmpty)
                eliminarTopAnimes()
            else{
                for(seguido in seguidos){
                    referenciaSeguidos.document(seguido.id).delete().addOnCompleteListener {
                        eliminarTopAnimes()
                    }
                }
            }

        }
    }

    private fun eliminarTopAnimes() {
        referenciaTopAnime.get().addOnSuccessListener { animes ->
            if(animes.isEmpty)
                eliminarListaSeguidoresAjeno()
            else {
                for (anime in animes) {
                    referenciaTopAnime.document(anime.id).delete().addOnCompleteListener {
                        eliminarListaSeguidoresAjeno()
                    }
                }
            }
        }
    }

    private fun eliminarListaSeguidoresAjeno() {
        referenciaUsuarios.get().addOnSuccessListener { usuarios ->
            for (usuario in usuarios) {
                referenciaUsuarios.document(usuario.id).collection("Seguidores").get()
                    .addOnSuccessListener { seguidores ->
                        if (seguidores.isEmpty) {
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
                eliminarPublicaciones()
            }
    }

    private fun eliminarPublicaciones() {
        referenciaPublicaciones
            .whereEqualTo("usuarioNombre", autentificacion.currentUser.displayName).get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    eliminarCredenciales()
                } else {
                    for (documento in it) {
                        referenciaPublicaciones
                            .document(documento.id).delete().addOnCompleteListener {
                                eliminarOpiniones(documento)
                            }
                    }
                }
            }
    }

    private fun eliminarOpiniones(documento: QueryDocumentSnapshot) {
        referenciaPublicaciones.document(documento.id).collection("opiniones").get()
            .addOnSuccessListener { it1 ->
                if (it1.isEmpty)
                    eliminarCredenciales()
                else {
                    for (opinion in it1) {
                        referenciaPublicaciones.document(documento.id).collection("opiniones")
                            .document(opinion.id).delete().addOnCompleteListener {
                                eliminarCredenciales()
                            }
                    }
                }
            }
    }

    private fun eliminarCredenciales() {
        usuarioConectado.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    circulo1_credenciales1.visibility = View.INVISIBLE
                    circulo1_credenciales2.visibility = View.INVISIBLE
                    finishAffinity()
                }
            }
    }
}