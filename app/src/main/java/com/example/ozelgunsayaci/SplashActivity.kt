package com.example.ozelgunsayaci

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : Activity() { // Standart Activity kullanıyoruz
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Tasarım dosyamızı bağlıyoruz (Kırmızı olabilir, takılma)
        setContentView(R.layout.activity_splash)

        // 2.5 saniye bekle ve ana sayfaya (MainActivity) geç
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2500)
    }
}