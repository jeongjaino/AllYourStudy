package kr.co.wap.allyourstudy.fragments.todo

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.wap.allyourstudy.MainActivity
import kr.co.wap.allyourstudy.R
import kr.co.wap.allyourstudy.databinding.FragmentTodoWriteBinding
import kr.co.wap.allyourstudy.room.RoomCalendar
import kr.co.wap.allyourstudy.room.RoomHelper
import java.text.SimpleDateFormat
import java.util.*


class TodoWriteFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    val binding by lazy{FragmentTodoWriteBinding.inflate(layoutInflater)}

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity){
            mainActivity = context
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadDate()
        setSpinner()
        binding.writeDoneButton.setOnClickListener{
            insertTodo()
        }
        binding.CalendarBtd.setOnClickListener{
            setDate()
        }
        return binding.root
    }
    private fun loadDate(){
        val sdf = SimpleDateFormat("MM월 dd일 E요일", Locale.KOREA)
        val date = sdf.format(System.currentTimeMillis())
        binding.writeDateText.text = date.toString()
    }
    private fun insertTodo(){
        val helper = RoomHelper.getInstance(mainActivity)
        val spinner = binding.spinner
        val date = binding.writeDateText.text.toString()
        val text = binding.writeTodoText.text.toString()
        val level = spinner.selectedItem.toString()
        val todo = RoomCalendar(date, text, level)
        CoroutineScope(Dispatchers.IO).launch {
            helper?.roomCalendarDao()?.insert(todo)
        }
        mainActivity.goTodoList()
    }
    private fun setSpinner(){
        val level = resources.getStringArray(R.array.todo_level_array)
        val spinner = binding.spinner
        ArrayAdapter.createFromResource(
            mainActivity,
            R.array.todo_level_array,
            android.R.layout.simple_spinner_item
        ).also{adapter->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }
    private fun setDate(){
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("기간을 설정하세요!")
                .setTheme(R.style.Theme_App)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        datePicker.addOnPositiveButtonClickListener {
            binding.writeDateText.text = datePicker.headerText.format(Locale.KOREA)
        }
        datePicker.show(mainActivity.supportFragmentManager, "datePicker")
    }
}