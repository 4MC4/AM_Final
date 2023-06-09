package com.android.example.automation_control_helper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentManager: FragmentManager
    private var currentFragmentTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentManager = supportFragmentManager

        if (savedInstanceState == null) {
            showFragment(FragmentA(), "FragmentA")
        } else {
            currentFragmentTag = savedInstanceState.getString("currentFragmentTag")
            currentFragmentTag?.let { tag ->
                val fragment = fragmentManager.findFragmentByTag(tag)
                fragment?.let {
                    showFragment(fragment, tag)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_fragment_a -> {
                showFragment(FragmentA(), "FragmentA")
                true
            }
            R.id.action_fragment_b -> {
                showFragment(FragmentB(), "FragmentB")
                true
            }
            R.id.action_fragment_c -> {
                showFragment(FragmentC(), "FragmentC")
                true
            }
            R.id.action_fragment_d -> {
                showFragment(FragmentD(), "FragmentD")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentFragmentTag", currentFragmentTag)
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, tag).commit()
        currentFragmentTag = tag
    }
}