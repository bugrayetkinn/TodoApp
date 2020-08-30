package com.yetkin.todoapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yetkin.todoapp.databinding.ActivityTodoAddBinding

class TodoAddActivity : AppCompatActivity() {

    private val todoAddActivityBinding: ActivityTodoAddBinding by lazy {
        ActivityTodoAddBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(todoAddActivityBinding.root)

        todoAddActivityBinding.apply {

            setSupportActionBar(toolbar2)
            supportActionBar?.setDisplayShowTitleEnabled(false)

            toolbar2.setNavigationOnClickListener {
                startActivity(Intent(this@TodoAddActivity, MainActivity::class.java))
                finish()
            }
        }


    }
}