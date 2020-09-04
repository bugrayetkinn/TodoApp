package com.yetkin.todoapp.ui.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.yetkin.todoapp.R
import com.yetkin.todoapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val mainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        val navigationController =
            Navigation.findNavController(
                this,
                R.id.nav_host_fragment_container
            )
        val navigationView = NavigationView(this)
        NavigationUI.setupWithNavController(navigationView, navigationController)

    }

}