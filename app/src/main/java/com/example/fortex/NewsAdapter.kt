package com.example.fortex

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewsAdapter(private val context: Context, private var newsList: List<NewsPost>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_list, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = newsList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    fun updateNewsList(newList: List<NewsPost>) {
        newsList = newList
        notifyDataSetChanged()
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gameNameTextView: TextView = itemView.findViewById(R.id.post_game_name_textView)
        private val titleTextView: TextView = itemView.findViewById(R.id.post_title_textView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.post_description_textView)
        private val likeNumberTextView: TextView = itemView.findViewById(R.id.post_like_number_textView)
        private val postImageView: ImageView = itemView.findViewById(R.id.post_imageView)
        private val likeImageView: ImageView = itemView.findViewById(R.id.post_like_imageView)
        private val deleteImageView: ImageView = itemView.findViewById(R.id.post_list_delete_imageView)
        private val commentsImageView: ImageView = itemView.findViewById(R.id.post_comments_imageView)

        fun bind(newsPost: NewsPost) {
            gameNameTextView.text = newsPost.gameName
            titleTextView.text = newsPost.title
            descriptionTextView.text = newsPost.description

            commentsImageView.setOnClickListener {
                val intent = Intent(context, Post_Comments_Activity::class.java).apply {
                    putExtra("gameName", newsPost.gameName)
                    putExtra("title", newsPost.title)
                    putExtra("description", newsPost.description)
                    putExtra("imageUrl", newsPost.imageUrl)
                    putExtra("postId", newsPost.postId)
                }
                context.startActivity(intent)
            }

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val uid = currentUser.uid
                val likeRef = db.collection("likes").document("${uid}_${newsPost.postId}")

                likeRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        likeImageView.setImageResource(R.drawable.baseline_favorite_24)
                    } else {
                        likeImageView.setImageResource(R.drawable.baseline_favorite_border_24)
                    }
                }.addOnFailureListener { e ->
                    Log.e("NewsAdapter", "Error getting like status", e)
                }
            }

            likeImageView.setOnClickListener {
                toggleLike(newsPost)
            }

            Glide.with(itemView)
                .load(newsPost.imageUrl)
                .into(postImageView)

            countLikes(newsPost)

            if (currentUser != null && currentUser.uid == newsPost.userId) {
                deleteImageView.visibility = View.VISIBLE
                deleteImageView.setOnClickListener {
                    showDeleteConfirmationDialog(newsPost)
                }
            } else {
                deleteImageView.visibility = View.GONE
            }
        }

        private fun countLikes(newsPost: NewsPost) {
            val postId = newsPost.postId
            val likesRef = db.collection("likes").whereEqualTo("postId", postId)

            likesRef.get().addOnSuccessListener { querySnapshot ->
                val likeCount = querySnapshot.size()
                likeNumberTextView.text = likeCount.toString()
            }
        }

        private fun toggleLike(newsPost: NewsPost) {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val likeRef = db.collection("likes").document("${uid}_${newsPost.postId}")

                likeRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        likeRef.delete().addOnSuccessListener {
                            newsPost.isLiked = false
                            notifyItemChanged(adapterPosition)
                        }
                    } else {
                        val likeData = hashMapOf("postId" to newsPost.postId, "userId" to uid)
                        likeRef.set(likeData).addOnSuccessListener {
                            newsPost.isLiked = true
                            notifyItemChanged(adapterPosition)
                        }
                    }
                }
            }
        }

        private fun showDeleteConfirmationDialog(newsPost: NewsPost) {
            val builder = AlertDialog.Builder(itemView.context)
            builder.apply {
                setMessage("Czy na pewno chcesz usunąć ten post?")
                setPositiveButton("Tak") { dialog, _ ->
                    deletePost(newsPost)
                    dialog.dismiss()
                }
                setNegativeButton("Nie") { dialog, _ ->
                    dialog.dismiss()
                }
            }
            val dialog = builder.create()
            dialog.show()
        }

        private fun deletePost(deletedPost: NewsPost) {
            val postId = deletedPost.postId
            db.collection("posts").document(postId)
                .delete()
                .addOnSuccessListener {
                    newsList = newsList.filter { it.postId != postId }
                    notifyDataSetChanged()

                    val likesQuery = db.collection("likes").whereEqualTo("postId", postId)
                    likesQuery.get().addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot) {
                            db.collection("likes").document(document.id).delete()
                        }
                    }.addOnFailureListener { e ->
                        Log.e("NewsAdapter", "Error deleting likes", e)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("NewsAdapter", "Error deleting post", e)
                }
        }
    }
}
