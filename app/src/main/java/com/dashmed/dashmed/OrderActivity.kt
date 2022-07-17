package com.dashmed.dashmed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.dashmed.dashmed.databinding.ActivityCartBinding
import com.dashmed.dashmed.databinding.ActivityOrderBinding
import com.dashmed.dashmed.fragments.OrderDetails
import com.dashmed.dashmed.fragments.Package

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderBinding.inflate(layoutInflater)
        setSupportActionBar(binding.orderToolbar)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = null
            setHomeAsUpIndicator(R.drawable.back_ind)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.order_container, OrderDetails())
                }.commit()
            } else { this.finish() }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}