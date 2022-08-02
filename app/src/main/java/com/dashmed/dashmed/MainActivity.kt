package com.dashmed.dashmed

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.dashmed.dashmed.databinding.ActivityMainBinding
import com.dashmed.dashmed.fragments.Account
import com.dashmed.dashmed.fragments.Cart
import com.dashmed.dashmed.fragments.Home
import com.dashmed.dashmed.fragments.Orders
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currFragment: Int = R.id.home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = null

        if (Utils.getUID(this) == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            this.finish()
        }

        setContentView(binding.root)

        binding.bottomNav.setOnItemSelectedListener {
            replaceFragment(it.itemId)
            true
        }
        binding.bottomNav.setOnItemReselectedListener { false }
    }

    override fun onBackPressed() {
        if (currFragment != R.id.home) {
            binding.bottomNav.selectedItemId = R.id.home
            currFragment = R.id.home
            return
        }
        super.onBackPressed()
    }

    private fun replaceFragment(itemID: Int) {
        supportFragmentManager.beginTransaction().apply {
            when (itemID) {
                R.id.home -> {
                    setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    replace(R.id.main_container, Home())
                }
                R.id.orders -> {
                    if (currFragment == R.id.home) {
                        setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    } else {
                        setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    }
                    replace(R.id.main_container, Orders())
                }
                R.id.cart -> {
                    if (currFragment == R.id.account) {
                        setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    } else {
                        setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                    replace(R.id.main_container, Cart())
                }
                R.id.account -> {
                    setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    replace(R.id.main_container, Account())
                }
            }

        }.commit()
        currFragment = itemID
    }
}