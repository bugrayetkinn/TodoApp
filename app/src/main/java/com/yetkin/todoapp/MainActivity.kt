package com.yetkin.todoapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
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
    private lateinit var todoAndDoneAdapter: TodoAndDoneAdapter
    private var listSize: Int = 0
    private lateinit var deleteIcon: Drawable
    private lateinit var iconBitmap: Bitmap


    private fun drawableToBitmap(drawable: Drawable): Bitmap {

        val bitmap: Bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        deleteIcon = ContextCompat.getDrawable(this, R.drawable.delete)!!
        iconBitmap = drawableToBitmap(deleteIcon)
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
        todoAndDoneAdapter = TodoAndDoneAdapter()

        mainBinding.apply {

            recyclerViewDays.setHasFixedSize(true)
            recyclerViewDays.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewDays.adapter = monthDayAdapter

            /**********************************************************************************/

            listSize = todoAndDoneAdapter.currentList.size
            recyclerViewTodo.setHasFixedSize(true)
            recyclerViewTodo.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewTodo.adapter = todoAndDoneAdapter
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(recyclerViewTodo)


            floatingActionButton.setOnClickListener {
                startActivityForResult(Intent(this@MainActivity, TodoAddActivity::class.java), 1)
            }
        }

        /**
         * TODO
         * change date click recycleritem(Day recycler)
         */
        todoViewModel.getTodo("31/08/2020").observe(this, Observer { list ->

            Log.e("Observe : ", "HERE")
            todoAndDoneAdapter.submitList(list)
            txtTodo.text = "TO DO (${list.size})"
        })
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
                val todoModel = todoAndDoneAdapter.removeItem(position)
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
        val simpleDataFormmatDayNumber = SimpleDateFormat("dd")
        val dayList = ArrayList<MonthDayModel>()


        calendar.set(year, month - 1, 1)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 0..daysInMonth) {

            val dayName = simpleDateFormatDayName.format(calendar.time)
            val dayNumber = simpleDataFormmatDayNumber.format(calendar.time)
            val result = dayNumber + "\n" + dayName
            val date = simpleDateFormat.format(calendar.time)

            val monthDayModel = MonthDayModel(date, result)
            dayList.add(monthDayModel)

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dayList
    }
}