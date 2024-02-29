package com.example.fortex

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class Post_Comments_Activity : AppCompatActivity() {

    private lateinit var gameNameTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var addCommentButton: Button
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var commentEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_comments)

        gameNameTextView = findViewById(R.id.post_comments_game_name_textView)
        titleTextView = findViewById(R.id.post_comments_title_textView)
        descriptionTextView = findViewById(R.id.post_comments_description_textView)
        commentsRecyclerView = findViewById(R.id.post_comments_recyclerView)
        addCommentButton = findViewById(R.id.post_comments_add_button)
        commentEditText = findViewById(R.id.post_comments_editTextText)

        commentsRecyclerView.layoutManager = LinearLayoutManager(this)

        val postId = intent.getStringExtra("postId") ?: ""
        commentsAdapter = CommentsAdapter(postId)
        commentsRecyclerView.adapter = commentsAdapter

        val gameName = intent.getStringExtra("gameName") ?: ""
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""

        gameNameTextView.text = gameName
        titleTextView.text = title
        descriptionTextView.text = description

        commentsAdapter.loadComments()

        Glide.with(this)
            .load(imageUrl)
            .into(findViewById(R.id.post_comments_game_imageView))

        addCommentButton.setOnClickListener {
            val commentText = commentEditText.text.toString()
            if (commentText.isNotEmpty()) {
                commentsAdapter.addComment(commentText)
                commentEditText.text.clear()
            } else {
                Toast.makeText(this, "Proszę wprowadź komentarz", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
