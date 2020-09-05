package com.yetkin.todoapp.ui.view.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yetkin.todoapp.MyAlertDialog
import com.yetkin.todoapp.R
import com.yetkin.todoapp.adapter.MonthDayAdapter
import com.yetkin.todoapp.adapter.TodoAndDoneAdapter
import com.yetkin.todoapp.adapter.model.MonthDayModel
import com.yetkin.todoapp.data.local.todo.TodoModel
import com.yetkin.todoapp.data.local.user.UserModel
import com.yetkin.todoapp.databinding.FirstPasswordDialogBinding
import com.yetkin.todoapp.databinding.SeconPasswordDialogBinding
import com.yetkin.todoapp.ui.viewmodel.HomeViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.first_password_dialog.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var monthDayAdapter: MonthDayAdapter
    private lateinit var todoAdapter: TodoAndDoneAdapter
    private lateinit var doneAdapter: TodoAndDoneAdapter
    private lateinit var calendar: Calendar
    private val date: MutableLiveData<String> = MutableLiveData()
    private lateinit var simpleDateFormat: SimpleDateFormat
    private var monthAndYear = ""
    private lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundle = Bundle()

        val isUpdate1 = arguments?.getInt("isUpdate1")
        val bundle = arguments?.getSerializable("todoModel")
        val bundle1 = arguments?.getSerializable("todoModelOld")

        /**
         * TODO room update fun dont work
         * Remove old item add new item
         */
        if (bundle != null) {
            val todoModel: TodoModel = bundle as TodoModel
            when (isUpdate1) {
                0 -> {
                    homeViewModel.insert(todoModel)
                }
                1 -> {
                    val todoModelOld: TodoModel = bundle1 as TodoModel
                    homeViewModel.delete(todoModelOld)
                    homeViewModel.insert(todoModel)
                }
            }
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

        val list: ArrayList<MonthDayModel> = printDate(year, month + 1)
        monthDayAdapter = MonthDayAdapter(list, setOnMonthDayItemClickListener)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtDateHome.text = monthAndYear

        layoutDate.setOnClickListener {
            showDateDialog(calendar, simpleDateFormat)
        }

        /**
         * Private password
         */

        imgPrivate.setOnClickListener {

            var dialogView: View? = null

            val preferences =
                requireContext().getSharedPreferences("passwordStatus", Context.MODE_PRIVATE)
            val pwStatus = preferences.getInt("pwStatus", 0)
            val editPreferences = preferences.edit()
            val myAlertDialog = MyAlertDialog(requireContext())

            when (pwStatus) {
                0 -> {

                    myAlertDialog.alertDialog.setTitle(R.string.pwAndMinCharacter)
                    myAlertDialog.alertDialog.setIcon(R.drawable.password)

                    val firstPasswordDialogBinding =
                        FirstPasswordDialogBinding.inflate(layoutInflater)
                    dialogView = firstPasswordDialogBinding.root

                    dialogView.apply {

                        btnFirstPwSave.setOnClickListener {

                            val pw1 = editTxtPw1.text.toString()
                            val pw2 = editTxtPw2.text.toString()

                            if (pw1.isNotEmpty() && pw2.isNotEmpty() && pw1.length >= 6 && pw2.length >= 6 && pw1 == pw2) {
                                Toast.makeText(requireContext(), "Succes", Toast.LENGTH_SHORT)
                                    .show()
                                homeViewModel.insertUser(UserModel(userPassword = pw1))
                                editPreferences.putInt("pwStatus", 1).apply()

                            } else {
                                Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show()
                            }
                            myAlertDialog.alertDialog.dismiss()
                        }

                        btnFirstPwCancel.setOnClickListener {
                            myAlertDialog.alertDialog.dismiss()
                        }
                    }
                }
                1 -> {

                    val secondPasswordDialogBinding =
                        SeconPasswordDialogBinding.inflate(layoutInflater)
                    dialogView = secondPasswordDialogBinding.root

                    secondPasswordDialogBinding.apply {

                        btnSecondPwOpen.setOnClickListener {
                            val password = editTxtSecondPw.text.toString()

                            if (password.isNotEmpty()) {
                                val passwordStatus = homeViewModel.passwordControl(password)
                                if (passwordStatus) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Succes Password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    myAlertDialog.alertDialog.dismiss()
                                    /**
                                     * password true private fragment open
                                     */
                                    NavHostFragment.findNavController(this@HomeFragment)
                                        .navigate(R.id.action_homeFragment_to_privateFragment)


                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Fail Password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        btnSecondPwClose.setOnClickListener {
                            myAlertDialog.alertDialog.dismiss()
                        }

                        txtResetPw.setOnClickListener {
                            Toast.makeText(requireContext(), "Reset?", Toast.LENGTH_SHORT).show()
                            myAlertDialog.alertDialog.dismiss()

                            val firstPasswordDialogBinding =
                                FirstPasswordDialogBinding.inflate(layoutInflater)
                            val dialogView2 = firstPasswordDialogBinding.root

                            val myAlertDialog2 = MyAlertDialog(requireContext())

                            myAlertDialog2.createDialog(dialogView2)

                            dialogView2.apply {

                                editTxtPw1.hint = "reset password"
                                editTxtPw2.hint = "reset password"
                                editTxtPwOld.visibility = View.VISIBLE

                                btnFirstPwSave.setOnClickListener {

                                    val pwOld = editTxtPwOld.text.toString()
                                    val pw1 = editTxtPw1.text.toString()
                                    val pw2 = editTxtPw2.text.toString()

                                    val oldPwControl = homeViewModel.passwordControl(pwOld)

                                    if (oldPwControl && pwOld.isNotEmpty() && pw1.isNotEmpty() && pw2.isNotEmpty() && pw1.length >= 6 && pw2.length >= 6 && pw1 == pw2) {
                                        Toast.makeText(
                                            requireContext(),
                                            "Succes",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        homeViewModel.deletePassword(pwOld)
                                        homeViewModel.insertUser(UserModel(userPassword = pw1))
                                        editPreferences.putInt("pwStatus", 1).apply()
                                        myAlertDialog2.alertDialog.dismiss()
                                    } else {
                                        Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }

                                btnFirstPwCancel.setOnClickListener {
                                    myAlertDialog2.alertDialog.dismiss()
                                }
                            }
                        }
                    }
                }
            }

            dialogView?.let { it1 -> myAlertDialog.createDialog(it1) }
        }

        /**********************************************************************************/

        recyclerViewMonthDayInitialize()

        /**********************************************************************************/

        recyclerViewTodoInitialize()

        /**********************************************************************************/

        recyclerViewDoneInitialize()

        /**********************************************************************************/

        floatingActionButton.setOnClickListener {
            bundle.putInt("isUpdate", 0)
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_homeFragment_to_todoAddFragment, bundle)
        }


        date.observe(viewLifecycleOwner, Observer { date ->

            homeViewModel.getTodo(date).observe(viewLifecycleOwner, Observer { list ->

                todoAdapter.submitList(list)
                txtTodo.text = "TO DO (${list.size})"
            })

            homeViewModel.getDone(date).observe(viewLifecycleOwner, Observer { list ->
                doneAdapter.submitList(list)
                txtDone.text = "DONE (${list.size})"
            })
        })


    }

    private fun recyclerViewMonthDayInitialize() {
        recyclerViewDays.setHasFixedSize(true)
        recyclerViewDays.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewDays.adapter = monthDayAdapter
    }

    private fun recyclerViewTodoInitialize() {
        recyclerViewTodo.setHasFixedSize(true)
        recyclerViewTodo.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        todoAdapter =
            TodoAndDoneAdapter(setOnCheckBoxClickListener, setOnTodoAndDoneItemClickListener)
        recyclerViewTodo.adapter = todoAdapter
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewTodo)
    }

    private fun recyclerViewDoneInitialize() {
        recyclerViewDone.setHasFixedSize(true)
        recyclerViewDone.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        doneAdapter =
            TodoAndDoneAdapter(setOnCheckBoxClickListener, setOnTodoAndDoneItemClickListener)
        recyclerViewDone.adapter = doneAdapter
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback1)
        itemTouchHelper.attachToRecyclerView(recyclerViewDone)
    }


    private val setOnMonthDayItemClickListener: (MonthDayModel) -> Unit = {
        date.value = it.date
    }
    private val setOnTodoAndDoneItemClickListener: (TodoModel) -> Unit = {
        bundle.putInt("isUpdate", 1)
        bundle.putSerializable("todoModel1", it)
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_homeFragment_to_todoAddFragment, bundle)
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
        homeViewModel.update(todoModelNew)
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

                val position = viewHolder.adapterPosition
                val todoModel = todoAdapter.removeItem(position)
                homeViewModel.delete(todoModel)
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

    private val itemTouchHelperCallback1 =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition
                val todoModel = doneAdapter.removeItem(position)
                homeViewModel.delete(todoModel)
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