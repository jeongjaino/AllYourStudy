package kr.co.wap.allyourstudy.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kr.co.wap.allyourstudy.databinding.FragmentLogoutDialogBinding

class LogoutDialogFragment : DialogFragment() {

    val binding by lazy{FragmentLogoutDialogBinding.inflate(layoutInflater)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding.logoutButtonYes.setOnClickListener {
            buttonClickListener.onButtonYesClicked()
            dismiss()
        }
        binding.logoutButtonNo.setOnClickListener {
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