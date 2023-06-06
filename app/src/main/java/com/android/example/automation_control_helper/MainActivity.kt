package com.android.example.automation_control_helper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentA()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_fragment_a -> {
                showFragment(FragmentA())
                true
            }
            R.id.action_fragment_b -> {
                showFragment(FragmentB())
                true
            }
            R.id.action_fragment_c -> {
                showFragment(FragmentC())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }
}