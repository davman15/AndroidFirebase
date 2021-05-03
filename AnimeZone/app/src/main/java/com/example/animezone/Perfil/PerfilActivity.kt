package com.example.animezone.Perfil

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.TextUtils
import android.text.TextUtils.isEmpty
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.animezone.Clase.Usuario
import com.example.animezone.ProgressBar.CargandoDialog
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_crear_publicacion.*
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.activity_registro.*

class PerfilActivity : AppCompatActivity() {
    //Codigo el que queramos
    private val file = 1
    //Raiz del Storage
    private val storageReferencia = Firebase.storage.getReference("ImagenesPerfil")
    //Selecciono el Uri de la imagen que va a elegir el usuario
    private var imagenUri: Uri? = null
    private var contador = 1
    private val baseDatos = Firebase.firestore
    private val autentificacion = Firebase.auth
    private val coleccionUsuarios = baseDatos.collection("Usuarios")
    private var campoNombreTextoPerfil = ""
    private var campoApellidosTextoPerfil = ""
    private var campoCorreoTextoPerfil = ""
    private var campoNombreIdTextoPerfil = ""
    private var campoContrasenaPerfil = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        //Enseñar ProgressBar
        val cargando = CargandoDialog(this)
        cargando.empezarCarga()
        val handler = Handler()
        handler.postDelayed({ cargando.cancelable() }, 1900)
        nombre_ImagenPerfil.setText(autentificacion.currentUser.displayName)

        //Enseñar Campos
        coleccionUsuarios.document(autentificacion.currentUser.displayName).get()
            .addOnSuccessListener {
                //Rellenar campos
                nombrePerfil_texto.setText(it.getString("nombreUsuario").toString())
                apellidosPerfil_texto.setText(it.getString("apellidos").toString())
                correoPerfil_texto.setText(it.getString("correo").toString())
                nicknamePerfil_texto.setText(it.getString("usuarioId").toString())
                contrasenaPerfil_texto.setText(it.getString("contrasena").toString())
                if(apellidosPerfil_texto.text.toString()=="null"){
                    apellidosPerfil_texto.setText("")
                }

                var urlImagen = it.getString("imagen").toString()
                Glide.with(this)
                    .load(urlImagen)
                    .fitCenter()
                    .into(imagenPerfil)
            }

        //Toast.makeText(this, autentificacion.currentUser.email, Toast.LENGTH_SHORT).show()


        //Para q este desactivado tiene q ser el desactivar false
        editarPerfil_btn.setOnClickListener {
            if (contador % 2 != 0) {
                imagenPerfil.isClickable = true
                imagenPerfil.setOnClickListener {
                    seleccionarFotoGaleria()
                }
                camposPerfil(true)
                editarPerfil_btn.setText("Guardar Cambios")
            } else {
                imagenPerfil.isClickable = false
                //Coger los campos
                campoNombreTextoPerfil = nombrePerfil_texto.text.toString()
                campoApellidosTextoPerfil = apellidosPerfil_texto.text.toString()
                campoCorreoTextoPerfil = correoPerfil_texto.text.toString()
                campoNombreIdTextoPerfil = nicknamePerfil_texto.text.toString()
                campoContrasenaPerfil = contrasenaPerfil_texto.text.toString()

                //Validar Campos
                when {
                    isEmpty(campoNombreTextoPerfil) -> {
                        nombrePerfil_texto.setError("Introduzca su nombre")
                        nombrePerfil_texto.requestFocus()
                        return@setOnClickListener
                    }
                    isEmpty(campoApellidosTextoPerfil) -> {
                        apellidosPerfil_texto.setError("Introduzca sus apellidos")
                        apellidosPerfil_texto.requestFocus()
                        return@setOnClickListener
                    }
                    isEmpty(campoCorreoTextoPerfil) -> {
                        correoPerfil_texto.setError("Introduzca su email")
                        correoPerfil_texto.requestFocus()
                        return@setOnClickListener
                    }
                    isEmpty(campoNombreIdTextoPerfil) -> {
                        nicknamePerfil_texto.setError("Introduzca su nickname")
                        nicknamePerfil_texto.requestFocus()
                        return@setOnClickListener
                    }
                    isEmpty(campoContrasenaPerfil) -> {
                        contrasenaPerfil_texto.setError("Introduzca su contraseña")
                        contrasenaPerfil_texto.requestFocus()
                        return@setOnClickListener
                    }
                }

                //Si esta bien actualiza los campos
                actualizarRegistros(
                    campoNombreTextoPerfil,
                    campoApellidosTextoPerfil,
                    campoCorreoTextoPerfil,
                    campoNombreIdTextoPerfil,
                    campoContrasenaPerfil
                )
                camposPerfil(false)
                editarPerfil_btn.setText("Editar Perfil")
            }
            contador++
        }
    }

    private fun camposPerfil(desactivador: Boolean) {
        nombrePerfil_texto.isEnabled = desactivador
        apellidosPerfil_texto.isEnabled = desactivador
        //contrasenaPerfil_texto.isEnabled = desactivador
    }

    private fun actualizarRegistros(
        nombre: String,
        apellidos: String,
        correo: String,
        usuarioId: String,
        contrasena: String
    ) {
        var foto = ""
        //Lo subimos al Storage
        if (imagenUri == null) {
            foto = autentificacion.currentUser.photoUrl.toString()
            actualizarPerfil(nombre, apellidos, correo, usuarioId, contrasena, foto)
        } else {
            //Aqui creo la subcarpeta de la referencia
            val folder = storageReferencia.child("{$usuarioId}/${imagenUri!!.lastPathSegment}")
            folder.putFile(imagenUri!!).addOnSuccessListener {
                //Si se sube bien al Storage, Creo el link con downloadURL
                folder.downloadUrl.addOnSuccessListener { urlImagen ->
                    foto = urlImagen.toString()
                    actualizarPerfil(nombre, apellidos, correo, usuarioId, contrasena, foto)
                }
            }
        }
    }

    private fun actualizarPerfil(
        nombre: String,
        apellidos: String,
        correo: String,
        usuarioId: String,
        contrasena: String,
        foto: String
    ) {
        //Meterlos en un objeto
        val usuario: Usuario = Usuario(
            nombre,
            apellidos,
            correo,
            usuarioId,
            contrasena,
            foto
        )
        baseDatos.collection("Usuarios").document(campoNombreIdTextoPerfil).set(usuario)
            .addOnSuccessListener {
                val cambiarNick = userProfileChangeRequest {
                    displayName = usuarioId
                    photoUri = Uri.parse(foto)
                }
                autentificacion.currentUser.updateProfile(cambiarNick)
                AlertDialog.Builder(this).apply {
                    setTitle("Perfil Actualizado")
                    setMessage("Los cambios fueron realizados correctamente")
                    setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>Aceptar</font>")) { _: DialogInterface, _: Int ->
                    }
                }.show()
            }
    }

    private fun seleccionarFotoGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        //Que seleccione solo imagenes de todas las extensiones
        intent.type = "image/*"
        startActivityForResult(intent, file)
    }

    //Sobreescribo el metodo para sobreescribir el valor de la Uri de la imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == file && resultCode == Activity.RESULT_OK -> {
                imagenUri = data!!.data
                imagenPerfil.setImageURI(imagenUri)
            }
        }
    }
}