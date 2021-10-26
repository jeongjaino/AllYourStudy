package kr.co.wap.allyourstudy.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import kr.co.wap.allyourstudy.R
import kr.co.wap.allyourstudy.databinding.TodoRecyclerItemBinding
import kr.co.wap.allyourstudy.frienddata.Account
import kr.co.wap.allyourstudy.room.RoomCalendar
import kr.co.wap.allyourstudy.room.RoomHelper

class TodoAdapter(/*private val listener: onClickListener*/): RecyclerView.Adapter<TodoAdapter.Holder>() {
    var todoList = mutableListOf<RoomCalendar>()
    val helper: RoomHelper? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            TodoRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val calendar = todoList.get(position)
        holder.setCalendar(calendar)
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    inner class Holder(val binding: TodoRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root)/*,
        View.OnClickListener */ {
        //lateinit var todoItem: RoomCalendar
        fun setCalendar(todo: RoomCalendar) {
            binding.todoText.text = todo.text
            binding.dateText.text = todo.date
            if (binding.todoLevelCheckBox.isChecked == true) {
                binding.todoText.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.todoText.paintFlags = 0
            }
            when (todo.level) {
                "HARD" -> {
                    binding.todoLevelCheckBox.setBackgroundColor(Color.rgb(237, 98, 98))
                }
                "NORMAL" -> {
                    binding.todoLevelCheckBox.setBackgroundColor(Color.rgb(237, 194, 110))
                }
                "EASY" -> {
                    binding.todoLevelCheckBox.setBackgroundColor(Color.rgb(149, 209, 188))
                }
            }
            //this.todoItem = todo
        }

        init {
            binding.todoLevelCheckBox.setOnClickListener {
                if (binding.todoLevelCheckBox.isChecked) {
                    binding.todoText.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    notifyDataSetChanged()

                } else {
                    binding.todoText.paintFlags = 0
                    notifyDataSetChanged()
                }
            }
            // binding.todoLevelCheckBox.setOnClickListener(this)
        }
        /*override fun onClick(view: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                if(view?.id == R.id.todo_cardView){
                    listener.onCardClick(position, todoItem)
                }
            }
        }*/
    }/*
    interface onClickListener{
        fun onCardClick(position: Int, todoItem: RoomCalendar)
    }*/

}