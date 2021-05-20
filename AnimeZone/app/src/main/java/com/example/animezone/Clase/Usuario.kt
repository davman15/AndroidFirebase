package com.example.animezone.Clase

class Usuario(
    val nombreUsuario: String? = null,
    val apellidos:String?=null,
    val correo: String? = null,
    var usuarioId: String? = null,
    val contrasena: String? = null,
    val imagen:String?=null,
    val descripcion:String?=null,
    val seguidores: ArrayList<String>? = arrayListOf()
)