package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : Activity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        //fridgeCollection = db.collection("users")

        emailEditText = findViewById(R.id.register_emailEditText)
        passwordEditText = findViewById(R.id.register_passwordEditText)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Podaj email i haslo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Rejestracja poprawna", Toast.LENGTH_SHORT).show()
                        val user = FirebaseAuth.getInstance().currentUser
                        val userEmail = user?.email.toString()
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("UserID", userEmail)
                        startActivity(intent)
                        val users = db.collection("users")
                        val data = hashMapOf(
                            "pomidor" to "1",
                            "żubr" to "100",
                            "jajka" to "0,5"
                        )

                        users.document(email).set(data)
                            .addOnSuccessListener {
                                Toast.makeText(this,"Dodano uzytkownika ",Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this,"Nie dodano uzytkownika",Toast.LENGTH_LONG).show()
                            }

                        finish()
                    } else {
                        Toast.makeText(this, "Blad: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
