package com.example.pruebafirebase

import com.google.firebase.firestore.Exclude
import java.util.*
import kotlin.collections.ArrayList

class Publicacion(val post:String?=null,
                  val fecha:Date?=null,
                  val usuarioNombre:String?=null,
                  val foto:String?=null,
                  val likes:ArrayList<String>?= arrayListOf()
                  ){
    //Esto es para que el id que siempre pone firebase al crear una publicacion no nos lo ponga
    @Exclude
    @set:Exclude
    @get:Exclude
    var uid:String?=null
    //Firebase necesita un constructor vacio para q el pueda añadir los atributos
    constructor():this(null,null,null,null,null)
}