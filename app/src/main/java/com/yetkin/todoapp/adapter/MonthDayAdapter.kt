package com.yetkin.todoapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yetkin.todoapp.databinding.DaysBinding
import com.yetkin.todoapp.model.MonthDayModel
import kotlinx.android.synthetic.main.days.view.*
import java.util.*


/**

Created by : Buğra Yetkin

Mail : bugrayetkinn@gmail.com

 */

class MonthDayAdapter(private val list: ArrayList<MonthDayModel>) :
    RecyclerView.Adapter<MonthDayAdapter.MonthDayHolder>() {

    private var index = -1

    class MonthDayHolder(private val daysBinding: DaysBinding) :
        RecyclerView.ViewHolder(daysBinding.root) {

        fun bind(monthDayModel: MonthDayModel) {
            daysBinding.apply {
                textView.text = monthDayModel.dayNameNumber
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthDayHolder =
        MonthDayHolder(DaysBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: MonthDayHolder, position: Int) {

        holder.bind(list[position])

        /**
         * Change backgrouncolor click item
         */
        holder.itemView.setOnClickListener {
            index = position
            notifyDataSetChanged()
        }

        if (index == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#9575cd"))
            holder.itemView.textView.setTextColor(Color.parseColor("#ffffff"))
        } else {
            holder.itemView.textView.setTextColor(Color.parseColor("#000000"))
            holder.itemView.setBackgroundColor(Color.WHITE)
        }
    }


    override fun getItemCount(): Int = list.size
}