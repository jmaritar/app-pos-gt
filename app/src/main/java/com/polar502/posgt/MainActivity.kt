package com.polar502.posgt

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.polar502.posgt.databinding.ActivityMainBinding
import com.polar502.posgt.fragment.CustomersFragment
import com.polar502.posgt.fragment.HomeFragment
import com.polar502.posgt.fragment.InventoryFragment
import com.polar502.posgt.fragment.OrdersFragment


class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_home -> {
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.fragmentcontainerview, HomeFragment())
                            commit()
                        }
                    }
                    R.id.nav_inventory -> {
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.fragmentcontainerview, InventoryFragment())
                            commit()
                        }
                    }
                    R.id.nav_orders -> {
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.fragmentcontainerview, OrdersFragment())
                            commit()
                        }
                    }
                    R.id.nav_customers -> {
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.fragmentcontainerview, CustomersFragment())
                            commit()
                        }
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }
}