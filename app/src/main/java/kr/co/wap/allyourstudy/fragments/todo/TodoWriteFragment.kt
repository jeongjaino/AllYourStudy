package kr.co.wap.allyourstudy.fragments.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
        binding.writeDateText.setOnClickListener{
            pickDateTime()
        }
        binding.privacyButton.setOnClickListener{
            if(binding.privacyButton.text == "Private"){
                binding.privacyButton.text = "Public"
            }
            else{
                binding.privacyButton.text ="Private"
            }
        }
        return binding.root
    }
    private fun loadDate(){
        val sdf = SimpleDateFormat("MM월 dd일 HH시 mm분", Locale.KOREA)
        val date = sdf.format(System.currentTimeMillis())
        binding.writeDateText.text = date.toString()
    }
    private fun insertTodo(){
        val helper = RoomHelper.getInstance(mainActivity)
        val spinner = binding.spinner
        val date = binding.writeDateText.text.toString()
        val text = binding.writeTodoText.text.toString()
        val level = spinner.selectedItem.toString()
        val todo = RoomCalendar(date, text, level, false)
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
    private fun pickDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)
        var dateFormat = SimpleDateFormat("MM월 dd일 HH시 mm분",Locale.KOREA)

        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                if(year.toString() != startYear.toString()){
                    dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분",Locale.KOREA)
                }
                val pickedDate = Calendar.getInstance()
                pickedDate.set(year, month, day, hour, minute)
                val pickedDateTime = pickedDate.time
                val date = dateFormat.format(pickedDateTime)
                binding.writeDateText.text = date
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }
}