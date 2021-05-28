package com.example.animezone.Perfil

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.animezone.Clase.Usuario
import com.example.animezone.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_perfil.*

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
    private var campoDescripcionTextoPerfil=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        circulo1.visibility= View.VISIBLE
        circulo2.visibility= View.VISIBLE
        //Enseñar Campos
        coleccionUsuarios.document(autentificacion.currentUser.displayName).get()
            .addOnSuccessListener {
                //Rellenar campos
                nombre_ImagenPerfil.text = it.getString("usuarioId").toString()
                nombrePerfil_texto.setText(it.getString("nombreUsuario").toString())
                apellidosPerfil_texto.setText(it.getString("apellidos").toString())
                correoPerfil_texto.setText(it.getString("correo").toString())
                nicknamePerfil_texto.setText(it.getString("usuarioId").toString())
                contrasenaPerfil_texto.setText(it.getString("contrasena").toString())
                descripcion_texto.setText(it.getString("descripcion").toString())
                if (apellidosPerfil_texto.text.toString() == "null")
                    apellidosPerfil_texto.setText("")

                if (descripcion_texto.text.toString() == "null")
                    descripcion_texto.setText("")


                var urlImagen = it.getString("imagen").toString()
                Glide.with(this)
                    .load(urlImagen)
                    .fitCenter()
                    .into(imagenPerfil)
                circulo1.visibility= View.INVISIBLE
                circulo2.visibility= View.INVISIBLE
            }

        //Toast.makeText(this, "ola", Toast.LENGTH_SHORT).show()

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
                circulo1.visibility= View.VISIBLE
                circulo2.visibility= View.VISIBLE
                imagenPerfil.isClickable = false
                //Coger los campos
                campoNombreTextoPerfil = nombrePerfil_texto.text.toString()
                campoApellidosTextoPerfil = apellidosPerfil_texto.text.toString()
                campoCorreoTextoPerfil = correoPerfil_texto.text.toString()
                campoNombreIdTextoPerfil = nicknamePerfil_texto.text.toString()
                campoContrasenaPerfil = contrasenaPerfil_texto.text.toString()
                campoDescripcionTextoPerfil=descripcion_texto.text.toString()

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
                    isEmpty(campoDescripcionTextoPerfil)->{
                        descripcion_texto.setError("Introduzca su descripción")
                        descripcion_texto.requestFocus()
                        return@setOnClickListener
                    }
                }

                //Si esta bien actualiza los campos
                actualizarRegistros(
                    campoNombreTextoPerfil,
                    campoApellidosTextoPerfil,
                    campoCorreoTextoPerfil,
                    campoNombreIdTextoPerfil,
                    campoContrasenaPerfil,
                    campoDescripcionTextoPerfil
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
        descripcion_texto.isEnabled=desactivador
    }

    private fun actualizarRegistros(
        nombre: String,
        apellidos: String,
        correo: String,
        usuarioId: String,
        contrasena: String,
        descripcion: String
    ) {
        var foto = ""
        //Lo subimos al Storage
        if (imagenUri == null) {
            foto = autentificacion.currentUser.photoUrl.toString()
            actualizarPerfil(nombre, apellidos, correo, usuarioId, contrasena, foto,descripcion)
        } else {
            //El apaño es el siguiente, la primera vez va a fallar en eliminar la foto ya que no existe esa referencia, entonces ira al addOnFailureListener
            storageReferencia.child("$usuarioId/" + autentificacion.currentUser.displayName.toString())
                .delete()
                .addOnSuccessListener {
                    crearReferenciaSustituir(usuarioId, foto, nombre, apellidos, correo, contrasena,descripcion)
                }
                .addOnFailureListener {
                    crearReferenciaSustituir(usuarioId, foto, nombre, apellidos, correo, contrasena,descripcion)
                }
        }
    }

    //Este metodo si no me equivoco es para que no se acumulen las fotos en el storage a la hora que se cambie la foto de perfil
    private fun crearReferenciaSustituir(
        usuarioId: String,
        foto: String,
        nombre: String,
        apellidos: String,
        correo: String,
        contrasena: String,
        descripcion: String
    ) {
        //Aqui creo la subcarpeta de la referencia
        var foto1 = foto
        val folder =
            storageReferencia.child("$usuarioId/" + autentificacion.currentUser.displayName.toString())
        folder.putFile(imagenUri!!).addOnSuccessListener {
            //Si se sube bien al Storage, Creo el link con downloadURL
            folder.downloadUrl.addOnSuccessListener { urlImagen ->
                foto1 = urlImagen.toString()
                actualizarPerfil(nombre, apellidos, correo, usuarioId, contrasena, foto1,descripcion)
            }
        }
    }

    private fun actualizarPerfil(
        nombre: String,
        apellidos: String,
        correo: String,
        usuarioId: String,
        contrasena: String,
        foto: String,
        descripcion: String
    ) {
        //Meterlos en un objeto
        val usuario: Usuario = Usuario(
            nombre,
            apellidos,
            correo,
            usuarioId,
            contrasena,
            foto,
            descripcion
        )

        nombre_ImagenPerfil.setText(autentificacion.currentUser.displayName)
        baseDatos.collection("Usuarios").document(campoNombreIdTextoPerfil).set(usuario)
            .addOnSuccessListener {
                val cambiarNick = userProfileChangeRequest {
                    displayName = usuarioId
                    photoUri = Uri.parse(foto)
                }
                autentificacion.currentUser.updateProfile(cambiarNick)
                circulo1.visibility= View.INVISIBLE
                circulo2.visibility= View.INVISIBLE
                Toast.makeText(this, "Perfil Actualizado Correctamente", Toast.LENGTH_SHORT).show()
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