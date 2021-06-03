package com.example.animezone.Chat

import java.util.*

data class Mensaje(
    var mensaje: String = "",
    var from: String = "",
    var fecha: Date = Date()
)
