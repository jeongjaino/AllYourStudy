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
            val hours = binding.hourPicker.value
            binding.hourText.text = "${if (hours < 10) "0" else ""}$hours:"
        }
        binding.minPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            val minutes = binding.minPicker.value
            binding.minText.text = "${if (minutes < 10) "0" else ""}$minutes:"
        }
        binding.secondPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            val seconds = binding.secondPicker.value
            binding.secondText.text = "${if (seconds < 10) "0" else ""}$seconds"
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