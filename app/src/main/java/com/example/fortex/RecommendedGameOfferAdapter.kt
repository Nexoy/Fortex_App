import com.example.fortex.GameOffer
import com.example.fortex.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecommendedGameOfferAdapter(private val offers: List<GameOffer>) : RecyclerView.Adapter<RecommendedGameOfferAdapter.ViewHolder>() {

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recommended_game_offer_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val offer = offers[position]
        holder.titleTextView.text = offer.title
        holder.priceTextView.text = "${offer.price} PLN"
        Glide.with(holder.itemView).load(offer.imageUrl).into(holder.imageView)

        // Set click listener
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
    }
}
