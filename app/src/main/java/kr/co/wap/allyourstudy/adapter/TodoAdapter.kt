package kr.co.wap.allyourstudy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.wap.allyourstudy.databinding.TodoRecyclerItemBinding
import kr.co.wap.allyourstudy.room.RoomCalendar

class TodoAdapter: RecyclerView.Adapter<Holder>() {
    var todoList = mutableListOf<RoomCalendar>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = TodoRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val calendar = todoList.get(position)
        holder.setCalendar(calendar)
    }

    override fun getItemCount(): Int {
        return todoList.size
    }
}

class Holder(val binding: TodoRecyclerItemBinding): RecyclerView.ViewHolder(binding.root){
    fun setCalendar(todo: RoomCalendar){
    }
}