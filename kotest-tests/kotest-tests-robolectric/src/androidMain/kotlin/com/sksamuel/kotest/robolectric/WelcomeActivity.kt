package com.sksamuel.kotest.robolectric

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class WelcomeActivity : Activity() {
    lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginButton = Button(this).apply { text = "Login" }
        setContentView(loginButton)
        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
