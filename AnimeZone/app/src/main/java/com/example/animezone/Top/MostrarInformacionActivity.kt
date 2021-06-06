package com.example.animezone.Top

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.animezone.R
import kotlinx.android.synthetic.main.activity_mostrar_informacion.*

class MostrarInformacionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mostrar_informacion)
        webView.webViewClient=object: WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url !=null){
                    view?.loadUrl(url)
                }
                return true
            }
        }
        val dato= intent.extras?.getString("url")
        if (dato != null)
            webView.loadUrl(dato)
    }
}