package kr.co.wap.allyourstudy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.co.wap.allyourstudy.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    val binding by lazy{FragmentHomeBinding.inflate(layoutInflater)}
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

    ): View? {

        return binding.root
    }
}