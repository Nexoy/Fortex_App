package com.example.fortex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class Notification_Activity : AppCompatActivity() {

    lateinit var notificationImageView: ImageView
    lateinit var homeImageView: ImageView
    lateinit var newsImageView: ImageView
    lateinit var backArrowImageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        notificationImageView  = findViewById(R.id.notification_notification_imageView)
        homeImageView  = findViewById(R.id.notification_home_imageView)
        newsImageView  = findViewById(R.id.notification_news_imageView)
        backArrowImageView = findViewById(R.id.notification_back_arrow_ImageView)

        newsImageView.setOnClickListener {
            val intent = Intent(this, News_Activity::class.java)
            startActivity(intent)
        }

        notificationImageView.setOnClickListener{
            val intent = Intent(this, Notification_Activity::class.java)
            startActivity(intent)
        }

        homeImageView.setOnClickListener{
            val intent = Intent(this, Home_Activity::class.java)
            startActivity(intent)
        }

        backArrowImageView.setOnClickListener {
            finish()
        }
    }
}