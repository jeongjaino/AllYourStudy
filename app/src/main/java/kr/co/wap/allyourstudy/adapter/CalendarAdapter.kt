package kr.co.wap.allyourstudy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.wap.allyourstudy.databinding.CalendarRecyclerItemBinding
import kr.co.wap.allyourstudy.room.RoomCalendar

class CalendarAdapter: RecyclerView.Adapter<Holder>() {
    var listData = mutableListOf<RoomCalendar>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = CalendarRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val calendar = listData.get(position)
        holder.setCalendar(calendar)
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}

class Holder(val binding: CalendarRecyclerItemBinding): RecyclerView.ViewHolder(binding.root){
    fun setCalendar(calendar: RoomCalendar){
        binding.itemTextDate.text = calendar.date
        binding.itemTextWeekDay.text = calendar.weekday
        binding.itemTextCheck.text = calendar.check
    }
}