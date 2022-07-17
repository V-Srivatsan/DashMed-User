package com.dashmed.dashmed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.coroutineScope
import com.dashmed.dashmed.data.DB
import com.dashmed.dashmed.databinding.ActivityCartBinding
import com.dashmed.dashmed.fragments.Package
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCartBinding.inflate(layoutInflater)
        setSupportActionBar(binding.cartToolbar)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = null
            setHomeAsUpIndicator(R.drawable.back_ind)
            setDisplayHomeAsUpEnabled(true)
        }
        binding.cartToolbarTitle.text = String.format(getString(R.string.cart_activity_title), intent.getStringExtra("package_name"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.cart_container, Package())
                }.commit()
            } else { this.finish() }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}