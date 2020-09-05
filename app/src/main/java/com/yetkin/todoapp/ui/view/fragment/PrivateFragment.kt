package com.yetkin.todoapp.ui.view.fragment

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yetkin.todoapp.R
import com.yetkin.todoapp.adapter.TodoAndDoneAdapter
import com.yetkin.todoapp.data.local.todo.TodoModel
import com.yetkin.todoapp.ui.viewmodel.HomeViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_private.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PrivateFragment : Fragment(R.layout.fragment_private) {

    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var todoPrivateAdapter: TodoAndDoneAdapter
    private lateinit var donePrivateAdapter: TodoAndDoneAdapter
    private lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundle = Bundle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarPrivate.setNavigationOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_privateFragment_to_homeFragment)
        }

        recyclerViewTodoPrivateInitialize()
        recyclerViewDonePrivateInitialize()

        getPrivateTodo()
        getPrivateDone()

    }

    private fun getPrivateTodo() {
        homeViewModel.getPrivateTodo().observe(viewLifecycleOwner, Observer {
            txtTodoPrivate.text = "TODO (${it.size})"
            todoPrivateAdapter.submitList(it)
        })
    }

    private fun getPrivateDone() {
        homeViewModel.getPrivateDone().observe(viewLifecycleOwner, Observer {
            txtDonePrivate.text = "DONE (${it.size})"
            donePrivateAdapter.submitList(it)

        })
    }

    private fun recyclerViewTodoPrivateInitialize() {
        recyclerViewTodoPrivate.setHasFixedSize(true)
        recyclerViewTodoPrivate.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        todoPrivateAdapter = TodoAndDoneAdapter(setOnCheckBoxClickListener, setOnItemClickListener)
        recyclerViewTodoPrivate.adapter = todoPrivateAdapter
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewTodoPrivate)
    }

    private fun recyclerViewDonePrivateInitialize() {
        recyclerViewDonePrivate.setHasFixedSize(true)
        recyclerViewDonePrivate.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        donePrivateAdapter =
            TodoAndDoneAdapter(setOnCheckBoxClickListener, setOnItemClickListener)
        recyclerViewDonePrivate.adapter = donePrivateAdapter
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback1)
        itemTouchHelper.attachToRecyclerView(recyclerViewDonePrivate)

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

    private val setOnItemClickListener: (TodoModel) -> Unit = {
        bundle.putInt("isUpdate", 1)
        bundle.putSerializable("todoModel1", it)
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_privateFragment_to_todoAddFragment, bundle)
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
                val todoModel = todoPrivateAdapter.removeItem(position)
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
                val todoModel = donePrivateAdapter.removeItem(position)
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
}