package com.yetkin.todoapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yetkin.todoapp.adapter.MonthDayAdapter
import com.yetkin.todoapp.adapter.TodoAndDoneAdapter
import com.yetkin.todoapp.data.local.TodoModel
import com.yetkin.todoapp.databinding.ActivityMainBinding
import com.yetkin.todoapp.model.MonthDayModel
import com.yetkin.todoapp.viewmodel.TodoViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val mainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val todoViewModel: TodoViewModel by viewModel()
    private lateinit var monthDayAdapter: MonthDayAdapter
    private lateinit var todoAdapter: TodoAndDoneAdapter
    private lateinit var doneAdapter: TodoAndDoneAdapter
    private var listSize: Int = 0
    private lateinit var calendar: Calendar
    private val date: MutableLiveData<String> = MutableLiveData()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
        onBackPressedDispatcher.addCallback(backPressedCallback)

        calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("MMMM yyyy")
        val simpleDateFormat1 = SimpleDateFormat("dd/MM/yyyy")
        date.value = simpleDateFormat1.format(calendar.time)
        Log.e("Date:", date.value)
        val monthAndYear = simpleDateFormat.format(calendar.time)
        txtDateHome.text = monthAndYear
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

        mainBinding.apply {

            layoutDate.setOnClickListener {
                showDateDialog(calendar, simpleDateFormat)
            }

            recyclerViewDays.setHasFixedSize(true)
            recyclerViewDays.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewDays.adapter = monthDayAdapter

            /**********************************************************************************/

            val layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            listSize = todoAdapter.currentList.size
            recyclerViewTodo.setHasFixedSize(true)
            recyclerViewTodo.layoutManager = layoutManager
            recyclerViewTodo.adapter = todoAdapter
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(recyclerViewTodo)

            /**********************************************************************************/

            recyclerViewDone.setHasFixedSize(true)
            recyclerViewDone.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewDone.adapter = doneAdapter

            /**********************************************************************************/


            floatingActionButton.setOnClickListener {
                startActivityForResult(Intent(this@MainActivity, TodoAddActivity::class.java), 1)
            }
        }

        /**
         * TODO
         * change date click recycleritem(Day recycler)
         */

        date.observe(this, Observer { date ->

            todoViewModel.getTodo(date).observe(this, Observer { list ->

                todoAdapter.submitList(list)
                txtTodo.text = "TO DO (${list.size})"
            })

            todoViewModel.getDone(date).observe(this, Observer { list ->
                doneAdapter.submitList(list)
                txtDone.text = "DONE (${list.size})"
            })

        })


    }

    private fun showDateDialog(calendar: Calendar, simpleDateFormat: SimpleDateFormat) {

        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            txtDateHome.text = simpleDateFormat.format(calendar.time)

            val list = printDate(year, month + 1)
            monthDayAdapter.refreshList(list)

            val simpleDateFormat1 = SimpleDateFormat("dd/MM/yyyy")
            val clickDate = simpleDateFormat1.format(calendar.time)
            date.value = clickDate
        }
        DatePickerDialog(
            this,
            datePickerListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
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
                            this@MainActivity,
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

    private val setOnItemClickListener: (MonthDayModel) -> Unit = {
        Log.e("Date Item : ", it.date)
        date.value = it.date
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            val model: TodoModel =
                data?.getBundleExtra("bundle")?.getSerializable("todoModel") as TodoModel

            /**
             * insert DB model
             */
            todoViewModel.insert(model)
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