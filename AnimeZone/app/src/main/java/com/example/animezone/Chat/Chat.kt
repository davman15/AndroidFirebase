package com.example.animezone.Chat

import java.util.*

data class Chat(
    var id: String = "",
    var nombre: String = "",
    var usuarios: List<String> = emptyList(),
    var fechaChat: Date =Date()
)
