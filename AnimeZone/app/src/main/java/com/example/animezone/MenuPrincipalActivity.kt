package com.example.animezone

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.animezone.Buscador.BuscadorActivity
import com.example.animezone.Chat.ListaChatsActivity
import com.example.animezone.Configuracion.ConfiguracionActivity
import com.example.animezone.Notificaciones.NotificacionesActivity
import com.example.animezone.Perfil.PerfilActivity
import com.example.animezone.Publicaciones.ListaPublicacionesActivity
import com.example.animezone.PublicacionesFavoritas.PublicacionesFavoritasActivity
import com.example.animezone.Seguidores.SeguidoresActivity
import com.example.animezone.Top.TopActivity
import com.example.animezone.Top.TopSeguidosActivity
import com.example.animezone.TusPublicaciones.TusPublicacionesActivity
import com.example.animezone.Wallpapers.WallpaperActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_menu_lateral.*
import kotlinx.android.synthetic.main.activity_menu_principal.*

class MenuPrincipalActivity : AppCompatActivity() {
    private val autentificacion = FirebaseAuth.getInstance()
    private var basedeDatos = Firebase.firestore
    private var referenciaNotificacionesnoLeidas =
        basedeDatos.collection("Usuarios")
            .document(autentificacion.currentUser.displayName)
            .collection("Notificaciones No Leidas")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        accionesMenuLateral()

        //Sustituyo el valor del TextView, respecto al usuario que esta conectado y su imagen de Perfil
        cargarDatosPersonales()

        //Abrir Menu Lateral
        imagenPerfilMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }
        accionesMenu()
        comprobarNotificaciones()
    }

    private fun accionesMenuLateral() {
        //Con esto llamo a mi cardview de la activity de MenuLateral
        val menuLateralCerrarSesion: CardView = findViewById(R.id.MenuLateralCerrarSesion)

        val menuLateralPerfil: CardView = findViewById(R.id.MenuLateralPerfil)
        menuLateralPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
        /*Y desde el Menu Principal hay que hacer los setonclickListener, si lo haces desde la activity MenuLateralActivity
         no te va el setonclicklistener*/
        menuLateralCerrarSesion.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Cierre de Sesión")
                setMessage("¿Estás seguro que quieres cerrar sesión?")
                setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Si</font>")) { dialogInterface: DialogInterface, i: Int ->
                    autentificacion.signOut()
                    finish()
                }
                setNegativeButton(Html.fromHtml("<font color='#FFFFFF'>No</font>"), null)
            }.show()
        }
        notificaciones_menuLateral.setOnClickListener {
            val intent = Intent(this, NotificacionesActivity::class.java)
            startActivity(intent)
            referenciaNotificacionesnoLeidas.get().addOnSuccessListener {
                if (it != null) {
                    for (notificacionNoLeida in it) {
                        referenciaNotificacionesnoLeidas.document(notificacionNoLeida.id).delete()
                    }
                }
            }
        }


        tusPublicaciones_menuLateral.setOnClickListener {
            val intent = Intent(this, TusPublicacionesActivity::class.java)
            startActivity(intent)
        }

        configuracion_menuLateral.setOnClickListener {
            val intent = Intent(this, ConfiguracionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun comprobarNotificaciones() {
        val iconoLateral: ImageView = findViewById(R.id.iconoverde_notificaciones_menuLateral)
        val contador_Notificaciones: TextView = findViewById(R.id.contador_Notificaciones_lateral)
        referenciaNotificacionesnoLeidas.addSnapshotListener { value, error ->
            if (value!!.isEmpty) {
                icono_verde_Menu.visibility = View.INVISIBLE
                contador_Notificaciones.setText("")
                iconoLateral.visibility = View.INVISIBLE
            } else {
                icono_verde_Menu.visibility = View.VISIBLE
                contador_Notificaciones.setText(value.size().toString())
                iconoLateral.visibility = View.VISIBLE
            }
        }
    }

    private fun accionesMenu() {
        //Para ir a la lista de Todas las publicaciones
        listaPublicaciones.setOnClickListener {
            val intent = Intent(this, ListaPublicacionesActivity::class.java)
            startActivity(intent)
        }
        //Ir a los Chats o para iniciar Conversacion
        listaChats.setOnClickListener {
            val intent = Intent(this, ListaChatsActivity::class.java)
            startActivity(intent)
        }
        //Ir al buscador
        buscadorUsuarios.setOnClickListener {
            val intent = Intent(this, BuscadorActivity::class.java)
            startActivity(intent)
        }
        //Ir a ver los seguidores que tiene el propio contacto
        ver_Seguidores.setOnClickListener {
            val intent = Intent(this, SeguidoresActivity::class.java)
            startActivity(intent)
        }

        listaPublicacionesFavoritas.setOnClickListener {
            val intent = Intent(this, PublicacionesFavoritasActivity::class.java)
            startActivity(intent)
        }

        verTop.setOnClickListener {
            val intent = Intent(this, TopActivity::class.java)
            startActivity(intent)
        }

        wallpapers_menu.setOnClickListener {
            val intent = Intent(this, WallpaperActivity::class.java)
            startActivity(intent)
        }

        verTopSeguidos.setOnClickListener {
            val intent = Intent(this, TopSeguidosActivity::class.java)
            startActivity(intent)
        }

    }


    //Esto hace que si el usuario, que ha ido a cambiarse la foto de perfil, cuando vuelva la tenga actualizada
    override fun onResume() {
        super.onResume()
        cargarDatosPersonales()
    }


    private fun cargarDatosPersonales() {
        if (autentificacion.currentUser != null) {
            usuarioActual.text = autentificacion.currentUser.displayName
            Glide.with(this).load(autentificacion.currentUser.photoUrl)
                .fitCenter()
                .into(imagenMenuLateralPerfil)
            basedeDatos.collection("Usuarios").document(autentificacion.currentUser.displayName)
                .addSnapshotListener { snapshot, e ->
                    Glide.with(applicationContext).load(snapshot!!.getString("imagen").toString()).fitCenter()
                        .into(imagenPerfilMenu)
                }
        } else {
            autentificacion.signOut()
            finish()
        }
    }
}