package kr.co.wap.allyourstudy.fragments.todo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kr.co.wap.allyourstudy.MainActivity
import kr.co.wap.allyourstudy.databinding.FragmentTodoListBinding

class TodoListFragment : Fragment() {

    private val binding by lazy{ FragmentTodoListBinding.inflate(layoutInflater)}

    private lateinit var mainActivity: MainActivity

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
        // 서버에서 저장된 투두 리스트 받아와서 list에 저장
        // 리사이클러뷰 어댑터와 어댑터 연결
        binding.writeButton.setOnClickListener {
            mainActivity.goTodoWrite()
        }
        return binding.root
    }
}