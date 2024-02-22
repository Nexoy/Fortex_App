package com.example.fortex

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class Add_Post_Activity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val storageRef = storage.reference

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        val gameNameEditText: EditText = findViewById(R.id.add_post_game_name_editTextText)
        val titleEditText: EditText = findViewById(R.id.add_post_title_editTextText)
        val descriptionEditText: EditText = findViewById(R.id.add_post_description_editTextText)
        val addButton: Button = findViewById(R.id.add_post_add_button)
        val addImageButton: Button = findViewById(R.id.add_post_image_button)

        addImageButton.setOnClickListener {
            openImageChooser()
        }

        addButton.setOnClickListener {
            val gameName = gameNameEditText.text.toString().trim()
            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            if (gameName.isNotEmpty() && title.isNotEmpty() && description.isNotEmpty() && selectedImageUri != null) {
                addPostToDatabase(gameName, title, description)
            } else {
                Toast.makeText(this, "Please fill in all fields and add an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                selectedImageUri = data.data
                Log.d("Add_Post_Activity", "Selected Image URI: $selectedImageUri")
            }
        }
    }

    private fun addPostToDatabase(gameName: String, title: String, description: String) {
        val filename = UUID.randomUUID().toString()
        val ref = storageRef.child("images/$filename")

        ref.putFile(selectedImageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    val post = hashMapOf(
                        "gameName" to gameName,
                        "title" to title,
                        "description" to description,
                        "imageUrl" to imageUrl,
                        "likes" to 0,
                        "comments" to 0
                    )

                    db.collection("posts")
                        .add(post)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "Post added successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to add post", Toast.LENGTH_SHORT).show()
                            Log.e("Add_Post_Activity", "Error adding post", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                Log.e("Add_Post_Activity", "Error uploading image", e)
            }
    }
}
