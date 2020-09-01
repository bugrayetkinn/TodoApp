package com.yetkin.todoapp.view.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.yetkin.todoapp.R
import com.yetkin.todoapp.data.local.TodoModel
import kotlinx.android.synthetic.main.fragment_todo_add.*
import java.text.SimpleDateFormat
import java.util.*

class TodoAddFragment : Fragment(R.layout.fragment_todo_add) {

    private var priority = -1
    private var isUpdate: Int? = 0
    private lateinit var todoModel1: TodoModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments?.getSerializable("todoModel1")
        if (bundle != null) {
            todoModel1 = bundle as TodoModel
        }
        isUpdate = arguments?.getInt("isUpdate", 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                NavHostFragment.findNavController(this@TodoAddFragment)
                    .navigate(R.id.action_todoAddFragment_to_homeFragment)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        toolbar2.setNavigationOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_todoAddFragment_to_homeFragment)
        }
        when (isUpdate) {
            0 -> {
                txtTopTitle.text = getString(R.string.add_new_task)
                buttonSave.text = getString(R.string.save)
            }
            1 -> {
                txtTopTitle.text = getString(R.string.update)
                buttonSave.text = getString(R.string.update)
                editTxtTitle.setText(todoModel1.title)
                editTxtMessage.setText(todoModel1.message)
                txtDatePicker.text = todoModel1.date
                txtTimePicker.text = todoModel1.time

                var color = 0

                when (todoModel1.priority) {
                    1 -> {
                        priority = 1
                        color = ContextCompat.getColor(requireContext(), R.color.colorBlue)
                    }
                    2 -> {
                        priority = 2
                        color = ContextCompat.getColor(requireContext(), R.color.colorGreen)
                    }
                    3 -> {
                        priority = 3
                        color = ContextCompat.getColor(requireContext(), R.color.colorRed)
                    }
                }
                toolbar2.setBackgroundColor(color)
            }
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
                    color = ContextCompat.getColor(requireContext(), R.color.colorBlue)
                }
                R.id.checkBoxGreen -> {
                    priority = 2
                    color = ContextCompat.getColor(requireContext(), R.color.colorGreen)
                }
                R.id.checkBoxRed -> {
                    priority = 3
                    color = ContextCompat.getColor(requireContext(), R.color.colorRed)
                }
            }
            toolbar2.setBackgroundColor(color)
        }
        buttonSave.setOnClickListener {

            val bundle = Bundle()
            val title = editTxtTitle.text.toString()
            val message = editTxtMessage.text.toString()
            val datePicker = txtDatePicker.text.toString()
            val timePicker = txtTimePicker.text.toString()

            val todoModel = TodoModel(
                title = title,
                message = message,
                date = datePicker,
                time = timePicker,
                priority = priority
            )

            // Check Empty
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

                when (isUpdate) {

                    0 -> {

                        //Save
                        bundle.putInt("isUpdate1", 0)
                        bundle.putSerializable("todoModel", todoModel)
                        NavHostFragment.findNavController(this)
                            .navigate(R.id.action_todoAddFragment_to_homeFragment, bundle)
                    }
                    1 -> {
                        //Update

                        /**
                         *  todoModel == todoModel1 problem uniqe id room
                         */
                        if (
                            todoModel.title == todoModel1.title
                            && todoModel.message == todoModel1.message
                            && todoModel.date == todoModel1.date
                            && todoModel.time == todoModel1.time
                            && todoModel.priority == todoModel1.priority
                        ) {
                            Toast.makeText(
                                requireContext(),
                                "Değişiklik Yapılmadı",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            bundle.putInt("isUpdate1", 1)
                            bundle.putSerializable("todoModelOld", todoModel1)
                            bundle.putSerializable("todoModel", todoModel)
                            NavHostFragment.findNavController(this)
                                .navigate(R.id.action_todoAddFragment_to_homeFragment, bundle)
                        }
                    }
                }


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