package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : Activity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        emailEditText = findViewById(R.id.login_emailEditText)
        passwordEditText = findViewById(R.id.login_passwordEditText)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                //brak wprowadzonych danych
                Toast.makeText(this, "Podaj email i haslo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // poprawne zalogowanie przejdz do HomeActivity
                        val user = FirebaseAuth.getInstance().currentUser
                        val UserID = user?.email.toString()
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("UserID", UserID)
                        startActivity(intent)
                        finish()
                    } else {
                        // niepoprawne zalogowanie
                        Toast.makeText(this, "niepoprawny email lub haslo", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
