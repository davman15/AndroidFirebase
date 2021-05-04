package com.example.animezone.Chat

data class Chat(
    var id: String = "",
    var nombre: String = "",
    var usuarios: List<String> = emptyList()
)