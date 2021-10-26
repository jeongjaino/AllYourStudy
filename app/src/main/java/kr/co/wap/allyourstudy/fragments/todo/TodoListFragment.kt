package kr.co.wap.allyourstudy.fragments.todo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.wap.allyourstudy.MainActivity
import kr.co.wap.allyourstudy.adapter.TodoAdapter
import kr.co.wap.allyourstudy.databinding.FragmentTodoListBinding
import kr.co.wap.allyourstudy.room.RoomCalendar
import kr.co.wap.allyourstudy.room.RoomHelper

class TodoListFragment : Fragment()/*, TodoAdapter.onClickListener */{

    private val binding by lazy{ FragmentTodoListBinding.inflate(layoutInflater)}

    private lateinit var helper: RoomHelper

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) mainActivity = context
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 서버에서 저장된 투두 리스트 받아와서 list에 저장
        // 리사이클러뷰 어댑터와 어댑터 연결
        setAdapter()
        binding.writeButton.setOnClickListener {
            mainActivity.goTodoWrite()
        }
        return binding.root
    }
    private fun setAdapter(){
        val adapter = TodoAdapter(/*this@TodoListFragment*/)

        helper = RoomHelper.getInstance(mainActivity)!!
        CoroutineScope(Dispatchers.IO).launch {
            adapter.todoList.addAll(helper.roomCalendarDao().getAll())
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(mainActivity)
    }
   /* override fun onCardClick(position: Int, todoItem: RoomCalendar) {

    }*/
}