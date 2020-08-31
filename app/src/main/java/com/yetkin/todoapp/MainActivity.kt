package com.yetkin.todoapp

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        /**
         * todo
         * Year and month get calendar
         */
        val list = printDate(2020, 8)

        monthDayAdapter = MonthDayAdapter(list, setOnItemClickListener)
        todoAdapter = TodoAndDoneAdapter(setOnCheckBoxClickListener)
        doneAdapter = TodoAndDoneAdapter(setOnCheckBoxClickListener)

        mainBinding.apply {

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
        todoViewModel.getTodo("01/09/2020").observe(this, Observer { list ->

            todoAdapter.submitList(list)
            txtTodo.text = "TO DO (${list.size})"
        })

        todoViewModel.getDone("01/09/2020").observe(this, Observer { list ->
            doneAdapter.submitList(list)
            txtDone.text = "DONE (${list.size})"
        })
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