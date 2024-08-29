package com.cat.prjnotifications

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.google.gson.Gson



class MainActivity : AppCompatActivity() {
    private lateinit var notificationHistory: MutableList<NotificationItem>
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<NotificationItem>
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Initialize the notification history
        notificationHistory = loadNotificationHistory()

        // Set up the ListView and its adapter
        listView = findViewById(R.id.listNotfi)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notificationHistory)
        listView.adapter = adapter

        createNotificationChannel(this)


        val btnlocal = findViewById<Button>(R.id.btn_LN)

        btnlocal.setOnClickListener {
            showNotification(this)
        }

        val settings = findViewById<ImageButton>(R.id.btnSettings)

        settings.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "your_channel_id"
            val channelName = "Your Channel Name"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel description"
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }
    fun showNotification(context: Context) {
        val sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPreferences.getBoolean("Notifications", true)

        if (notificationsEnabled) {
            val title = "Your Notification Title"
            val message = "Your notification message."
            val timestamp = System.currentTimeMillis()

            // Create and display the notification
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )
            val notificationId = 1
            val builder = NotificationCompat.Builder(context, "your_channel_id")
                .setSmallIcon(R.drawable.baseline_star_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }

            // Add the notification to the history, update the ListView, and save it
            notificationHistory.add(NotificationItem(title, message, timestamp))
            adapter.notifyDataSetChanged()  // Notify the adapter to refresh the ListView
            saveNotificationHistory(notificationHistory)
        }
    }

    private fun loadNotificationHistory(): MutableList<NotificationItem> {
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val json = sharedPreferences.getString("NotificationHistory", "[]")
        val type = object : com.google.gson.reflect.TypeToken<MutableList<NotificationItem>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun saveNotificationHistory(history: List<NotificationItem>) {
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(history)
        editor.putString("NotificationHistory", json)
        editor.apply()
    }
}
