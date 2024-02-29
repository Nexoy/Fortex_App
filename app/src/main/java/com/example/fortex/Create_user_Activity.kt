package com.example.fortex

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class Create_user_Activity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser

        val emailEdit: EditText = findViewById(R.id.create_email_editTextText)
        val passwordEdit: EditText = findViewById(R.id.create_editTextTextPassword)
        val nameEdit: EditText = findViewById(R.id.create_name_editTextText)
        val addImageButton: Button = findViewById(R.id.create_add_image_button)
        val registerButton: Button = findViewById(R.id.create_button)

        addImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1001)
        }

        registerButton.setOnClickListener {
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()
            val name = nameEdit.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && selectedImageUri != null) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = auth.currentUser
                            val filename = UUID.randomUUID().toString()
                            val ref = storage.reference.child("user/$filename")

                            ref.putFile(selectedImageUri!!)
                                .addOnSuccessListener { taskSnapshot ->
                                    ref.downloadUrl.addOnSuccessListener { uri ->
                                        val imageUrl = uri.toString()

                                        val newUser = hashMapOf(
                                            "uid" to user!!.uid,
                                            "name" to name,
                                            "imageUrl" to imageUrl
                                        )

                                        db.collection("users")
                                            .add(newUser)
                                            .addOnSuccessListener {
                                                Log.d(TAG, "User added to Firestore successfully")
                                                Toast.makeText(
                                                    this,
                                                    "User created successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                val intent = Intent(this, MainActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w(TAG, "Error adding user to Firestore", e)
                                                Toast.makeText(
                                                    this,
                                                    "Error creating user",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error uploading image to Firebase Storage", e)
                                    Toast.makeText(
                                        this,
                                        "Error uploading image",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    baseContext,
                    "Please fill all the fields and select an image",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (data != null && data.data != null) {
                selectedImageUri = data.data
            } else {
                Log.e("CreateUserActivity", "Selected image URI is null")
            }
        }
    }
}