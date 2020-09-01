package com.yetkin.todoapp.view.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.yetkin.todoapp.R
import com.yetkin.todoapp.data.local.TodoModel
import kotlinx.android.synthetic.main.fragment_todo_add.*
import java.text.SimpleDateFormat
import java.util.*

class TodoAddFragment : Fragment(R.layout.fragment_todo_add) {

    private var priority = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar2.setNavigationOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_todoAddFragment_to_homeFragment)
        }

        val calendar = Calendar.getInstance()

        txtDatePicker.setOnClickListener {
            showDateDialog(calendar)
        }

        txtTimePicker.setOnClickListener {
            showTimeDialog(calendar)
        }

        radioGroupCheckbox.setOnCheckedChangeListener { _, checkedId ->

            var color = 0

            when (checkedId) {
                R.id.checkBoxBlue -> {
                    priority = 1
                    color = resources.getColor(R.color.colorBlue)
                }
                R.id.checkBoxGreen -> {
                    priority = 2
                    color = resources.getColor(R.color.colorGreen)
                }
                R.id.checkBoxRed -> {
                    priority = 3
                    color = resources.getColor(R.color.colorRed)
                }
            }
            toolbar2.setBackgroundColor(color)
        }
        buttonSave.setOnClickListener {

            val title = editTxtTitle.text.toString()
            val message = editTxtMessage.text.toString()
            val datePicker = txtDatePicker.text.toString()
            val timePicker = txtTimePicker.text.toString()

            if (TextUtils.isEmpty(title)
                || TextUtils.isEmpty(message)
                || TextUtils.isEmpty(datePicker)
                || TextUtils.isEmpty(timePicker)
                || priority == -1

            ) {
                Toast.makeText(
                    requireContext(),
                    "Boş alanları doldurun lütfen !",
                    Toast.LENGTH_LONG
                ).show()
            } else {

                val todoModel = TodoModel(
                    title = title,
                    message = message,
                    date = datePicker,
                    time = timePicker,
                    priority = priority
                )

                val bundle = Bundle()
                bundle.putSerializable("todoModel", todoModel)
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_todoAddFragment_to_homeFragment, bundle)

            }
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

            requireContext(),
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
            requireContext(),
            datePickerListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}