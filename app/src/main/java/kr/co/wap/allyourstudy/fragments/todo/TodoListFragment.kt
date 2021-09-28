package kr.co.wap.allyourstudy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kr.co.wap.allyourstudy.databinding.FragmentTodoListBinding

class TodoListFragment : Fragment() {

    private val binding by lazy{ FragmentTodoListBinding.inflate(layoutInflater)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.writeButton.setOnClickListener {

        }
        return binding.root
    }
}