package com.example.animezone.Notificaciones

import java.util.*

data class Notificacion(
    var usuarioId: String? = null,
    var mensaje:String?=null,
    var fecha:Date=Date(),
    var id:String?=null
)