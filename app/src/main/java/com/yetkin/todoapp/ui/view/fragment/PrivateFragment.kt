package com.yetkin.todoapp.ui.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.yetkin.todoapp.R
import com.yetkin.todoapp.adapter.TodoAndDoneAdapter
import com.yetkin.todoapp.data.local.todo.TodoModel
import com.yetkin.todoapp.ui.viewmodel.HomeViewModel
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

        recyclerViewTodoPrivate.setHasFixedSize(true)
        recyclerViewTodoPrivate.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        todoPrivateAdapter = TodoAndDoneAdapter(setOnCheckBoxClickListener, setOnItemClickListener)
        recyclerViewTodoPrivate.adapter = todoPrivateAdapter

        recyclerViewDonePrivate.setHasFixedSize(true)
        recyclerViewDonePrivate.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        donePrivateAdapter =
            TodoAndDoneAdapter(setOnCheckBoxClickListener, setOnItemClickListener)
        recyclerViewDonePrivate.adapter = donePrivateAdapter



        homeViewModel.getPrivateTodo().observe(viewLifecycleOwner, Observer {
            txtTodoPrivate.text = "TODO (${it.size})"
            todoPrivateAdapter.submitList(it)
        })

        homeViewModel.getPrivateDone().observe(viewLifecycleOwner, Observer {
            txtDonePrivate.text = "DONE (${it.size})"
            donePrivateAdapter.submitList(it)

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
        homeViewModel.update(todoModelNew)
    }

    private val setOnItemClickListener: (TodoModel) -> Unit = {
        bundle.putInt("isUpdate", 1)
        bundle.putSerializable("todoModel1", it)
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_privateFragment_to_todoAddFragment, bundle)
    }
}