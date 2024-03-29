package com.example.fortex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SpecialGameOfferAdapter(val offers: List<GameOffer>) : RecyclerView.Adapter<SpecialGameOfferAdapter.ViewHolder>() {

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.special_game_offer_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val offer = offers[position]
        holder.titleTextView.text = offer.title
        holder.priceTextView.text = "${offer.price} PLN"
        holder.durationTextView.text = offer.duration
        Glide.with(holder.itemView).load(offer.imageUrl).into(holder.imageView)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(offer.title)
        }
    }

    override fun getItemCount(): Int {
        return offers.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.recommended_game_imageView)
        val titleTextView: TextView = itemView.findViewById(R.id.recommended_game_offer_title_textView)
        val priceTextView: TextView = itemView.findViewById(R.id.recommended_game_price_textView)
        val durationTextView: TextView = itemView.findViewById(R.id.special_game_offer_time_textView)
    }
}
