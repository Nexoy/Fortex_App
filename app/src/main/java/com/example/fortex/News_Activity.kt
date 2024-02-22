
package com.example.fortex

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class News_Activity : AppCompatActivity() {

    lateinit var newsAdapter: NewsAdapter
    val db = FirebaseFirestore.getInstance()
    lateinit var newsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        newsRecyclerView = findViewById(R.id.news_recyclerView)
        newsRecyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(this, listOf())
        newsRecyclerView.adapter = newsAdapter

        fetchNewsPosts()

        val newsButton: Button = findViewById(R.id.news_add_post_button)

        newsButton.setOnClickListener {
            val intent = Intent(this, Add_Post_Activity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchNewsPosts() {
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                val newsList = mutableListOf<NewsPost>()
                for (document in result) {
                    val gameName = document.getString("gameName") ?: ""
                    val title = document.getString("title") ?: ""
                    val description = document.getString("description") ?: ""
                    val likeNumber = document.getLong("likeNumber")?.toInt() ?: 0
                    val commentNumber = document.getLong("commentNumber")?.toInt() ?: 0
                    val imageUrl = document.getString("imageUrl") ?: ""

                    val newsPost = NewsPost(gameName, title, description, likeNumber, commentNumber, imageUrl)
                    newsList.add(newsPost)
                }
                newsAdapter.updateNewsList(newsList)
            }
    }
}
