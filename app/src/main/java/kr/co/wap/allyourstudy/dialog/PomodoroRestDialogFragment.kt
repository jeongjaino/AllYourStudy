package kr.co.wap.allyourstudy.dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kr.co.wap.allyourstudy.R
import kr.co.wap.allyourstudy.databinding.FragmentPomodoroRestDialogBinding

class PomodoroRestDialogFragment : DialogFragment() {

    val binding by lazy{FragmentPomodoroRestDialogBinding.inflate(layoutInflater)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding.restButtonYes.setOnClickListener {
            buttonClickListener.onButtonYesClicked()
            dismiss()
        }
        return binding.root
    }
    interface OnButtonClickListener{
        fun onButtonYesClicked()
    }

    private lateinit var buttonClickListener: OnButtonClickListener

    fun setButtonClickListener(buttonClickListener: OnButtonClickListener){
        this.buttonClickListener = buttonClickListener
    }
}