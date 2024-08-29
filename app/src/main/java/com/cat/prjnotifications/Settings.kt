package com.cat.prjnotifications

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Settings : AppCompatActivity() {

    private lateinit var DisableNotifications: Switch
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val Home = findViewById<ImageButton>(R.id.btnBack)

        Home.setOnClickListener {
            finish()
        }

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)

        DisableNotifications = findViewById(R.id.NotifiTog)
        DisableNotifications.isChecked = sharedPreferences.getBoolean("Notifications", false)

        DisableNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("Notifications", isChecked).apply()
            recreate()  // Restart activity to apply theme change


        }
    }
}