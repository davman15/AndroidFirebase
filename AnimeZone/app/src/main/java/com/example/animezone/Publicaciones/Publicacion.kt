package com.example.animezone.Publicaciones

import com.google.firebase.firestore.Exclude
import java.util.*
import kotlin.collections.ArrayList

class Publicacion(
    val post: String? = null,
    val fecha: Date? = null,
    var usuarioNombre: String? = null,
    val foto: String? = null,
    val fotoPerfil: String?=null,
    val titulo:String?=null,
    val likes: ArrayList<String>? = arrayListOf()
) {
    //Esto es para que el id que siempre pone firebase al crear una publicacion no nos lo ponga
    @Exclude
    @set:Exclude
    @get:Exclude
    var uid: String? = null
}