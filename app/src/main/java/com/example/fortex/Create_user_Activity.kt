package com.example.fortex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Create_user_Activity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        auth = Firebase.auth

        val currentUser = auth.currentUser
        val db = Firebase.firestore

        val emailEdit: EditText = findViewById(R.id.create_email_editTextText)
        val passwordEdit: EditText = findViewById(R.id.create_editTextTextPassword)
        val nameEdit: EditText = findViewById(R.id.create_name_editTextText)
        val registerButton: Button = findViewById(R.id.create_button)


        registerButton.setOnClickListener {
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()
            val name = nameEdit.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser

                            val newUser = hashMapOf(
                                "uid" to user!!.uid,
                                "name" to name
                            )

                            db.collection("users")
                                .add(newUser)
                                .addOnSuccessListener {
                                    Log.d("Register", "DocumentSnapshot successfully written!")
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Register", "Error writing document", e)
                                    Toast.makeText(
                                        baseContext,
                                        "Error writing document",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            Log.w("TAG", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(baseContext, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

}