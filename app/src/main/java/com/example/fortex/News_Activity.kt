package com.example.fortex

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fortex.R.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class News_Activity : AppCompatActivity() {

    lateinit var newsAdapter: NewsAdapter
    val db = FirebaseFirestore.getInstance()
    lateinit var newsRecyclerView: RecyclerView
    lateinit var newsImageView: ImageView
    lateinit var notificationImageView: ImageView
    lateinit var homeImageView: ImageView
    lateinit var newsButton: Button
    lateinit var backArrowImageView: ImageView
    lateinit var commentsImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_news)

        newsButton = findViewById(id.news_add_post_button)
        homeImageView = findViewById(id.notification_home_imageView)
        notificationImageView = findViewById(id.notification_notification_imageView)
        backArrowImageView = findViewById(id.news_back_arrow_imageView)
        newsImageView = findViewById(id.notification_news_imageView)
        newsRecyclerView = findViewById(id.news_recyclerView)
        newsRecyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(this, listOf())
        newsRecyclerView.adapter = newsAdapter

        fetchNewsPosts()

        newsButton.setOnClickListener {
            val intent = Intent(this, Add_Post_Activity::class.java)
            startActivity(intent)
        }

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

    private fun fetchNewsPosts() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val currentUserId = currentUser.uid

            db.collection("posts")
                .get()
                .addOnSuccessListener { result ->
                    val newsList = mutableListOf<NewsPost>()
                    for (document in result) {
                        val postId = document.id
                        val gameName = document.getString("gameName") ?: ""
                        val title = document.getString("title") ?: ""
                        val description = document.getString("description") ?: ""
                        val imageUrl = document.getString("imageUrl") ?: ""

                        val newsPost = NewsPost(postId, gameName, title, description, imageUrl, false, currentUserId)
                        newsList.add(newsPost)
                    }
                    newsAdapter.updateNewsList(newsList)
                }
        } else {
            Log.e("News_Activity", "User is not authenticated")
        }
    }
}

