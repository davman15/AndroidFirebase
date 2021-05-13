package com.example.animezone.Buscador

import android.os.Build
import com.example.animezone.Clase.Usuario
import java.util.stream.Collectors

class prueba {
    private val usuariosLista: MutableList<Usuario>? = null
    private val originalUsuarios: List<Usuario>? = null
    fun filter(caracterBuscador: String) {
        if (caracterBuscador.length == 0) {
            usuariosLista!!.clear()
            usuariosLista.addAll(originalUsuarios!!)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val collect = usuariosLista!!.stream().filter { i: Usuario ->
                    i.nombreUsuario!!.toLowerCase().contains(caracterBuscador)
                }
                    .collect(Collectors.toList())
                usuariosLista.clear()
                usuariosLista.addAll(collect)
            }
        }
    }
}