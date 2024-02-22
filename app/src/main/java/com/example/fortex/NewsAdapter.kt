package com.example.fortex

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NewsAdapter(private val context: Context, private var newsList: List<NewsPost>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

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
        val gameNameTextView: TextView = itemView.findViewById(R.id.post_game_name_textView)
        val titleTextView: TextView = itemView.findViewById(R.id.post_title_textView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.post_description_textView)
        val likeNumberTextView: TextView = itemView.findViewById(R.id.post_like_number_textView)
        val commentNumberTextView: TextView = itemView.findViewById(R.id.post_comments_number_textView)
        val postImageView: ImageView = itemView.findViewById(R.id.post_imageView)
        val deleteImageView: ImageView = itemView.findViewById(R.id.post_list_delete_imageView)

        fun bind(newsPost: NewsPost) {
            gameNameTextView.text = newsPost.gameName
            titleTextView.text = newsPost.title
            descriptionTextView.text = newsPost.description
            likeNumberTextView.text = newsPost.likeNumber.toString()
            commentNumberTextView.text = newsPost.commentNumber.toString()

            Glide.with(itemView)
                .load(newsPost.imageUrl)
                .into(postImageView)
        }
    }
}
