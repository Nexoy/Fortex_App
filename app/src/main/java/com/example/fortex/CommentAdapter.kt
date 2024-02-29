package com.example.fortex

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommentsAdapter(private val postId: String) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var commentsList = mutableListOf<Comment>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(commentsList[position])
    }

    override fun getItemCount(): Int {
        return commentsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Comment>) {
        commentsList.clear()
        commentsList.addAll(list)
        notifyDataSetChanged()
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.comment_user_name_textView)
        private val commentTextView: TextView = itemView.findViewById(R.id.comment_user_comment_textView)
        private val deleteImageView: ImageView = itemView.findViewById(R.id.comment_item_delete_imageView)
        private val commentNumberTextView: TextView = itemView.findViewById(R.id.post_comments_number_textView)


        fun bind(comment: Comment) {
            userNameTextView.text = comment.name
            commentTextView.text = comment.commentText

            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null && currentUser.uid == comment.userId) {
                deleteImageView.visibility = View.VISIBLE
                deleteImageView.setOnClickListener {
                    showDeleteConfirmationDialog(comment)
                }
            } else {
                deleteImageView.visibility = View.GONE
            }

            countComments(comment)

        }
        private fun countComments(comment: Comment) {
            val postId = comment.postId
            val commentRef = db.collection("comments").whereEqualTo("postId", postId)

            commentRef.get().addOnSuccessListener { querySnapshot ->
                val likeCount = querySnapshot.size()
                commentNumberTextView.text = likeCount.toString()
            }
        }



        private fun showDeleteConfirmationDialog(comment: Comment) {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("Usuwanie komentarza")
            builder.setMessage("Czy na pewno chcesz usunąć ten komentarz?")

            builder.setPositiveButton("Tak") { dialog, which ->
                deleteComment(comment)
            }

            builder.setNegativeButton("Anuluj") { dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun deleteComment(comment: Comment) {
        db.collection("comments").document(comment.id)
            .delete()
            .addOnSuccessListener {
                Log.d("CommentsAdapter", "Komentarz usunięty pomyślnie")
            }
            .addOnFailureListener { e ->
                Log.e("CommentsAdapter", "Błąd podczas usuwania komentarza", e)
            }
    }

    fun loadComments() {
        db.collection("comments")
            .whereEqualTo("postId", postId)
            .get()
            .addOnSuccessListener { snapshot ->
                val newCommentsList = mutableListOf<Comment>() // Utwórz nową listę komentarzy

                for (document in snapshot.documents) {
                    val comment = document.toObject(Comment::class.java)
                    if (comment != null) {
                        db.collection("users")
                            .whereEqualTo("uid", comment.userId)
                            .get()
                            .addOnSuccessListener { userQuerySnapshot ->
                                if (!userQuerySnapshot.isEmpty) {
                                    val userDocument = userQuerySnapshot.documents[0]
                                    val name = userDocument.getString("name")
                                    val updatedComment = comment.copy(name = name ?: "Nieznany")
                                    newCommentsList.add(updatedComment)
                                    submitList(newCommentsList)
                                } else {
                                    Log.e("CommentsAdapter", "Nie znaleziono użytkownika o UID: ${comment.userId}")
                                }
                            }
                            .addOnFailureListener { e ->
                                // Obsługa błędu pobierania nazwy użytkownika
                                Log.e("CommentsAdapter", "Błąd podczas pobierania danych użytkownika", e)
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("CommentsAdapter", "Błąd podczas wczytywania komentarzy", exception)
            }
    }

    fun addComment(commentText: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users")
                .whereEqualTo("uid", currentUser.uid)
                .get()
                .addOnSuccessListener { userQuerySnapshot ->
                    if (!userQuerySnapshot.isEmpty) {
                        val userDocument = userQuerySnapshot.documents[0]
                        val name = userDocument.getString("name")
                        if (name != null) {
                            Log.d("CommentsAdapter", "Nazwa użytkownika: $name")

                            val comment = Comment(
                                postId = postId,
                                userId = currentUser.uid,
                                name = name,
                                commentText = commentText,
                                id = db.collection("comments").document().id // Przypisanie nowego ID
                            )

                            db.collection("comments").document(comment.id).set(comment)
                                .addOnSuccessListener {
                                    // Pomyślnie dodano komentarz
                                    Log.d("CommentsAdapter", "Komentarz dodany pomyślnie")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("CommentsAdapter", "Błąd podczas dodawania komentarza", e)
                                }
                        } else {
                            Log.e("CommentsAdapter", "Brak nazwy użytkownika")
                        }
                    } else {
                        Log.e("CommentsAdapter", "Nie znaleziono użytkownika o UID: ${currentUser.uid}")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("CommentsAdapter", "Błąd podczas pobierania danych użytkownika", e)
                }
        }
    }
}
