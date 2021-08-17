package kr.co.wap.allyourstudy.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kr.co.wap.allyourstudy.databinding.FragmentResetDialogBinding

class ResetDialogFragment : DialogFragment() {

    private val binding by lazy{ FragmentResetDialogBinding.inflate(layoutInflater)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding.resetButtonYes.setOnClickListener {
            buttonClickListener.onButtonYesClicked()
            dismiss()
        }
        binding.resetButtonNo.setOnClickListener {
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