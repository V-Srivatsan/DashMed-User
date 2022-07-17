package com.dashmed.dashmed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dashmed.dashmed.databinding.ActivityLoginBinding
import com.dashmed.dashmed.fragments.Login

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}