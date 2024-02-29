package com.example.fortex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class Account_info_Activity : AppCompatActivity() {

    lateinit var backArrowImageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_info)

        backArrowImageView = findViewById(R.id.account_back_arrow_imageView)


        backArrowImageView.setOnClickListener {
            finish()
        }
    }
}