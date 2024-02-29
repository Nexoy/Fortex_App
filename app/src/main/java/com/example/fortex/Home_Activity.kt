package com.example.fortex

import RecommendedGameOfferAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class Home_Activity : AppCompatActivity() {
    lateinit var recommendedOffersRecyclerView: RecyclerView
    lateinit var specialOffersRecyclerView: RecyclerView
    lateinit var bestOfferImageView: ImageView
    lateinit var bestOfferPriceTextView: TextView
    lateinit var notificationImageView: ImageView
    lateinit var homeImageView: ImageView
    lateinit var newsImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recommendedOffersRecyclerView = findViewById(R.id.home_recommended_recyclerView)
        specialOffersRecyclerView = findViewById(R.id.home_special_offers_recyclerView)
        bestOfferImageView = findViewById(R.id.home_bestoffert_imageView)
        bestOfferPriceTextView = findViewById(R.id.home_bestoffer_price_textView)
        notificationImageView  = findViewById(R.id.notification_notification_imageView)
        homeImageView  = findViewById(R.id.notification_home_imageView)
        newsImageView  = findViewById(R.id.notification_news_imageView)

        recommendedOffersRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        specialOffersRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        fetchRecommendedOffersFromFirebase()

        fetchSpecialOffersFromFirebase()

        fetchBestOfferFromFirebase()

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


    }

    private fun fetchRecommendedOffersFromFirebase() {
        val offersCollection = FirebaseFirestore.getInstance().collection("offers")
        offersCollection.whereEqualTo("type", "recommended").get()
            .addOnSuccessListener { documents ->
                val recommendedOffersList = mutableListOf<GameOffer>()
                for (document in documents) {
                    val title = document.getString("title") ?: ""
                    val price = document.getDouble("price") ?: 0.0
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val duration = document.getString("duration") ?: ""

                    val offer = GameOffer(title, price, imageUrl, duration)
                    recommendedOffersList.add(offer)
                }
                val recommendedAdapter = RecommendedGameOfferAdapter(recommendedOffersList)
                recommendedAdapter.setOnItemClickListener { title ->
                    val intent = Intent(this, Game_offer_Activity::class.java)
                    intent.putExtra("title", title)
                    startActivity(intent)
                }
                recommendedOffersRecyclerView.adapter = recommendedAdapter
            }
            .addOnFailureListener { exception ->
                Log.e("Home_Activity", "Error getting recommended offers", exception)
            }
    }

    private fun fetchSpecialOffersFromFirebase() {
        val offersCollection = FirebaseFirestore.getInstance().collection("offers")
        offersCollection.whereEqualTo("type", "special").get()
            .addOnSuccessListener { documents ->
                val specialOffersList = mutableListOf<GameOffer>()
                for (document in documents) {
                    val title = document.getString("title") ?: ""
                    val price = document.getDouble("price") ?: 0.0
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val duration = document.getString("duration") ?: ""

                    val offer = GameOffer(title, price, imageUrl, duration)
                    specialOffersList.add(offer)
                }
                val specialAdapter = SpecialGameOfferAdapter(specialOffersList)
                specialAdapter.setOnItemClickListener { title ->
                    val intent = Intent(this, Game_offer_Activity::class.java)
                    intent.putExtra("title", title)
                    startActivity(intent)
                }
                specialOffersRecyclerView.adapter = specialAdapter
            }
            .addOnFailureListener { exception ->
                Log.e("Home_Activity", "Error getting special offers", exception)
            }

    }

    private fun fetchBestOfferFromFirebase() {
        val offersCollection = FirebaseFirestore.getInstance().collection("offers")
        offersCollection.whereEqualTo("type", "best").get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val title = document.getString("title") ?: ""
                    val price = document.getDouble("price") ?: 0.0
                    val imageUrl = document.getString("imageUrl") ?: ""

                    bestOfferImageView.setOnClickListener {
                        val intent = Intent(this, Game_offer_Activity::class.java)
                        intent.putExtra("title", title)
                        startActivity(intent)
                    }

                    Glide.with(this).load(imageUrl).into(bestOfferImageView)
                    bestOfferPriceTextView.text = "$price"
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Home_Activity", "Error getting best offer", exception)
            }
    }

}
