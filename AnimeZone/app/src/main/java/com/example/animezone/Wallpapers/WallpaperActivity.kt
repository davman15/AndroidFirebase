package com.example.animezone.Wallpapers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.animezone.R
import kotlinx.android.synthetic.main.activity_wallpaper.*


class WallpaperActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper)

        //Array de Animes
        var imagenesAnimes = intArrayOf(
            R.drawable.kimi, R.drawable.sao, R.drawable.kimetsu,
            R.drawable.jujutsu, R.drawable.hunter, R.drawable.naruto,
            R.drawable.onepiece, R.drawable.haikyuu, R.drawable.onepunch
        )

        //Las descripciones que van con cada foto
        var titulosAnimes = arrayOf("Kimi No Nawa", "Sword Art Online",
            "Kimetsu no Yaiba", "Jujutsu Kaisen", "Hunter x Hunter",
            "Naruto Shippuden", "One Piece", "Haikyuu",
            "One Punch Man"
        )

        //Array de Paisajes
        var imagenesPaisajes = intArrayOf(
            R.drawable.fondoanime, R.drawable.estrellado, R.drawable.wallpaper, R.drawable.pantano,
            R.drawable.rio, R.drawable.ciudad, R.drawable.ciudad, R.drawable.atardecer2, R.drawable.ciudadatardecer
        )

        var imagenesMangas= intArrayOf(R.drawable.noragami_manga,R.drawable.deathnote_manga,R.drawable.berserk_manga,
        R.drawable.bleach_manga,R.drawable.bokunohero_manga,R.drawable.blackclover_manga,R.drawable.fate_manga)

        var titulosMangas = arrayOf(
            "Noragami", "Death Note", "Berserk", "Bleach",
            "Boku No Hero Academia", "Black Clover", "Fate Grand Order"
        )

        carrouselAnimes(titulosAnimes, imagenesAnimes)
        carrouselPaisajes(imagenesPaisajes)
        carrouselMangas(titulosMangas, imagenesMangas)
    }

    private fun carrouselAnimes(titulosAnimes: Array<String>, imagenesAnimes: IntArray) {
        carruselAnimesFamosos.pageCount = titulosAnimes.size
        //Coloco las imagenes en el carrusel
        carruselAnimesFamosos.setImageListener { posicion, imageView ->
            imageView.setImageResource(imagenesAnimes[posicion])
        }
        //Si tu haces click a la imagen te da la descripcion mediante un toast
        carruselAnimesFamosos.setImageClickListener { posicion ->
            Toast.makeText(applicationContext, titulosAnimes[posicion], Toast.LENGTH_SHORT).show()
        }
    }

    private fun carrouselPaisajes(imagenesPaisajes: IntArray) {
        //Si no le decimos cuanto es su longitud no furula
        carruselPaisajes.pageCount = imagenesPaisajes.size
        //Coloco las imagenes en el
        carruselPaisajes.setImageListener { posicion, imageView ->
            imageView.setImageResource(imagenesPaisajes[posicion])
        }
    }

    private fun carrouselMangas(titulosMangas: Array<String>, imagenesMangas: IntArray) {
        //Si no le decimos cuanto es su longitud no furula
        carruselMangas.pageCount = titulosMangas.size
        carruselMangas.setImageClickListener { posicion ->
            Toast.makeText(applicationContext, titulosMangas[posicion], Toast.LENGTH_SHORT).show()
        }
        //Coloco las imagenes en el
        carruselMangas.setImageListener { posicion, imageView ->
            imageView.setImageResource(imagenesMangas[posicion])
        }
    }
}
