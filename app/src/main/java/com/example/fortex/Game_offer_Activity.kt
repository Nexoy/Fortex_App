package com.example.fortex

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class Game_offer_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_offer)

        val title = intent.getStringExtra("title")

        val db = FirebaseFirestore.getInstance()
        val offersCollection = db.collection("offers")
        offersCollection.whereEqualTo("title", title).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val developer = document.getString("developer") ?: ""
                    val released = document.getString("released") ?: ""
                    val description = document.getString("description") ?: ""
                    val price = document.getDouble("price") ?: 0.0

                    findViewById<TextView>(R.id.offer_game_title_textView).text = title
                    findViewById<TextView>(R.id.offer_developer_textView).text = "Developer: $developer"
                    findViewById<TextView>(R.id.offer_released_textView).text = "Released: $released"
                    findViewById<TextView>(R.id.offer_description_textView).text = "Description: $description"
                    findViewById<TextView>(R.id.offer_price_textView).text = "Price: $price PLN"

                    val imageUrl = document.getString("imageUrl")
                    imageUrl?.let {
                        val imageView = findViewById<ImageView>(R.id.offer_game_imageView)
                        Glide.with(this).load(it).into(imageView)
                    }
                } else {
                    Log.e("Game_offer_Activity", "No offer found with title: $title")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Game_offer_Activity", "Error getting offer", exception)
            }
    }
}
