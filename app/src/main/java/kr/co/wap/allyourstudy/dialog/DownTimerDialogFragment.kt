package kr.co.wap.allyourstudy.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kr.co.wap.allyourstudy.databinding.FragmentDownTimerDialogBinding

class DownTimerDialogFragment : DialogFragment() {

    val binding by lazy{ FragmentDownTimerDialogBinding.inflate(layoutInflater)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.hourPicker.maxValue = 12
        binding.hourPicker.minValue = 0
        binding.minPicker.maxValue = 59
        binding.minPicker.minValue = 0
        binding.secondPicker.maxValue = 59
        binding.secondPicker.minValue = 0

        binding.hourPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            val hour = binding.hourPicker.value
            binding.hourText.text = hour.toString().plus(":")
        }
        binding.minPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            val min = binding.minPicker.value
            binding.minText.text = min.toString().plus(":")
        }
        binding.secondPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            val second = binding.secondPicker.value
            binding.secondText.text = second.toString()
        }
        binding.buttonYes.setOnClickListener {
            buttonClickListener.onButtonYesClicked()
            dismiss()
        }
        binding.buttonNo.setOnClickListener{
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