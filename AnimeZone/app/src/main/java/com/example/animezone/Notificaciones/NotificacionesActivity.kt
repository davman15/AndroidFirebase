package com.example.animezone.Notificaciones

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animezone.Chat.ListaChatsActivity
import com.example.animezone.Clase.Usuario
import com.example.animezone.Perfil.PerfilActivity
import com.example.animezone.Perfil.PerfilAjenoActivity
import com.example.animezone.Publicaciones.ListaPublicacionesActivity
import com.example.animezone.R
import com.example.animezone.Seguidores.SeguidoresActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_notificaciones.*

class NotificacionesActivity : AppCompatActivity() {
    private var autentificacion = Firebase.auth
    private var baseDatos = Firebase.firestore
    private var mensajeSeguir="te ha empezado a seguir"
    private var mensajeChat="quiere empezar una nueva conversación contigo"
    private var mensajeLikes="te ha dado un like en tu publicacion"
    private var mensajePublicacion="Subió una nueva publicación, venga corre a ver que novedades tiene"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificaciones)
        notificaciones_circulo2.visibility= View.VISIBLE
        notificaciones_circulo1.visibility= View.VISIBLE
        listaNotificaciones_rv.layoutManager=LinearLayoutManager(this)
        listaNotificaciones_rv.adapter=NotificacionAdapter{notificacion ->
            seleccionarNotificacion(notificacion)
        }

        baseDatos.collection("Usuarios").document(autentificacion.currentUser.displayName).collection("Notificaciones")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
            val listaNotificaciones= value?.toObjects(Notificacion::class.java)
            if (listaNotificaciones != null) {
                (listaNotificaciones_rv.adapter as NotificacionAdapter).listaActualizadaNotificaciones(listaNotificaciones)
                notificaciones_circulo2.visibility= View.INVISIBLE
                notificaciones_circulo1.visibility= View.INVISIBLE
            }
        }
    }

    fun seleccionarNotificacion(notificacion: Notificacion) {
        when {
            notificacion.mensaje!!.contains(mensajeChat) -> irChat()
            notificacion.mensaje!!.contains(mensajeLikes) -> irPublicaciones()
            notificacion.mensaje!!.contains(mensajeSeguir) -> irSeguidor(notificacion)
            notificacion.mensaje!!.contains(mensajePublicacion) -> irPublicaciones()
        }
    }

    private fun irChat(){
        val intent=Intent(this,ListaChatsActivity::class.java)
        startActivity(intent)
    }

    private fun irPublicaciones(){
        val intent=Intent(this,ListaPublicacionesActivity::class.java)
        startActivity(intent)
    }

    private fun irSeguidor(usuario: Notificacion) {
        val intent = Intent(this, PerfilAjenoActivity::class.java)
        intent.putExtra("UsuarioChat", usuario.usuarioId.toString())
        startActivity(intent)
    }

}