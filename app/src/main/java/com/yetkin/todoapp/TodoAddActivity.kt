package com.yetkin.todoapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yetkin.todoapp.databinding.ActivityTodoAddBinding
import kotlinx.android.synthetic.main.activity_todo_add.*
import java.text.SimpleDateFormat
import java.util.*

class TodoAddActivity : AppCompatActivity() {

    private val todoAddActivityBinding: ActivityTodoAddBinding by lazy {
        ActivityTodoAddBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(todoAddActivityBinding.root)

        setSupportActionBar(toolbar2)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val calendar = Calendar.getInstance()

        toolbar2.setNavigationOnClickListener {
            startActivity(Intent(this@TodoAddActivity, MainActivity::class.java))
            finish()
        }

        txtDatePicker.setOnClickListener {
            showDateDialog(calendar)
        }

        txtTimePicker.setOnClickListener {
            showTimeDialog(calendar)
        }

    }

    private fun showTimeDialog(calendar: Calendar) {

        val timePickerListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val simpleDateFormat = SimpleDateFormat("HH:mm")

            txtTimePicker.text = simpleDateFormat.format(calendar.time)

        }

        TimePickerDialog(
            this,
            timePickerListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun showDateDialog(calendar: Calendar) {

        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

            txtDatePicker.text = simpleDateFormat.format(calendar.time)
        }
        DatePickerDialog(
            this,
            datePickerListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}