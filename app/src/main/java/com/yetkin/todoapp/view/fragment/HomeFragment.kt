package com.yetkin.todoapp.view.fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yetkin.todoapp.R
import com.yetkin.todoapp.adapter.MonthDayAdapter
import com.yetkin.todoapp.adapter.TodoAndDoneAdapter
import com.yetkin.todoapp.data.local.TodoModel
import com.yetkin.todoapp.model.MonthDayModel
import com.yetkin.todoapp.viewmodel.TodoViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val todoViewModel: TodoViewModel by viewModel()
    private lateinit var monthDayAdapter: MonthDayAdapter
    private lateinit var todoAdapter: TodoAndDoneAdapter
    private lateinit var doneAdapter: TodoAndDoneAdapter
    private lateinit var calendar: Calendar
    private val date: MutableLiveData<String> = MutableLiveData()
    private lateinit var simpleDateFormat: SimpleDateFormat
    private var monthAndYear = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val d = arguments?.getSerializable("todoModel")
        if (d != null) {
            val todoModel: TodoModel = d as TodoModel
            todoViewModel.insert(todoModel)
        }


        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)

        calendar = Calendar.getInstance()
        simpleDateFormat = SimpleDateFormat("MMMM yyyy")
        val simpleDateFormat1 = SimpleDateFormat("dd/MM/yyyy")
        date.value = simpleDateFormat1.format(calendar.time)
        monthAndYear = simpleDateFormat.format(calendar.time)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        /**
         * todo
         * Year and month get calendar
         */
        val list: ArrayList<MonthDayModel> = printDate(year, month + 1)

        monthDayAdapter = MonthDayAdapter(list, setOnItemClickListener)
        todoAdapter = TodoAndDoneAdapter(setOnCheckBoxClickListener)
        doneAdapter = TodoAndDoneAdapter(setOnCheckBoxClickListener)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtDateHome.text = monthAndYear

        layoutDate.setOnClickListener {
            showDateDialog(calendar, simpleDateFormat)
        }

        /**********************************************************************************/

        recyclerViewDays.setHasFixedSize(true)
        recyclerViewDays.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewDays.adapter = monthDayAdapter

        /**********************************************************************************/

        recyclerViewTodo.setHasFixedSize(true)
        recyclerViewTodo.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerViewTodo.adapter = todoAdapter
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewTodo)

        /**********************************************************************************/

        recyclerViewDone.setHasFixedSize(true)
        recyclerViewDone.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerViewDone.adapter = doneAdapter

        /**********************************************************************************/

        floatingActionButton.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_homeFragment_to_todoAddFragment)
        }


        date.observe(viewLifecycleOwner, Observer { date ->

            todoViewModel.getTodo(date).observe(viewLifecycleOwner, Observer { list ->

                todoAdapter.submitList(list)
                txtTodo.text = "TO DO (${list.size})"
            })

            todoViewModel.getDone(date).observe(viewLifecycleOwner, Observer { list ->
                doneAdapter.submitList(list)
                txtDone.text = "DONE (${list.size})"
            })

        })


    }


    private val setOnItemClickListener: (MonthDayModel) -> Unit = {
        date.value = it.date
        /**
         *  detailFragment
         */
    }

    private val setOnCheckBoxClickListener: (TodoModel) -> Unit = { todoModel ->

        var todoModelNew = todoModel.copy()
        when (todoModel.checkDone) {
            0 -> {
                todoModelNew = todoModel.copy(checkDone = 1)
            }
            1 -> {
                todoModelNew = todoModel.copy(checkDone = 0)
            }
        }
        todoViewModel.update(todoModelNew)
    }

    private fun showDateDialog(calendar: Calendar, simpleDateFormat: SimpleDateFormat) {

        var list: ArrayList<MonthDayModel> = ArrayList()

        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            txtDateHome.text = simpleDateFormat.format(calendar.time)

            list.clear()
            list = printDate(year, month + 1)
            monthDayAdapter.refreshList(list)

            val simpleDateFormat1 = SimpleDateFormat("dd/MM/yyyy")
            val clickDate = simpleDateFormat1.format(calendar.time)
            date.value = clickDate
        }
        DatePickerDialog(
            requireContext(),
            datePickerListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }


    private val itemTouchHelperCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                Log.e("OnSwiped : ", "HERE")
                val position = viewHolder.adapterPosition
                val todoModel = todoAdapter.removeItem(position)
                todoViewModel.delete(todoModel)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addSwipeLeftBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPink
                        )
                    )
                    .addSwipeLeftActionIcon(R.drawable.delete)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .create()
                    .decorate()


                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }

    private fun printDate(year: Int, month: Int): ArrayList<MonthDayModel> {
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val simpleDateFormatDayName = SimpleDateFormat("EEEE")
        val simpleDataFormatDayNumber = SimpleDateFormat("dd")
        val dayList = ArrayList<MonthDayModel>()


        calendar.set(year, month - 1, 1)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 0..daysInMonth) {

            val dayName = simpleDateFormatDayName.format(calendar.time)
            val dayNumber = simpleDataFormatDayNumber.format(calendar.time)
            val result = dayNumber + "\n" + dayName
            val date = simpleDateFormat.format(calendar.time)

            val monthDayModel = MonthDayModel(date, result)
            dayList.add(monthDayModel)

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dayList
    }
}