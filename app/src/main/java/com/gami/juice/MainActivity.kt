package com.gami.juice

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            text = "Gami APK build is working. Integrate gameplay scene here."
            textSize = 20f
            setPadding(48, 96, 48, 48)
        }

        setContentView(textView)
    }
}
