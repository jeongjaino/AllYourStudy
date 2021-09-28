package kr.co.wap.allyourstudy.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import kr.co.wap.allyourstudy.databinding.SpinnerTimerItemBinding
import java.lang.Exception

class TimerSpinner(context: Context,
@LayoutRes private val resId: Int, private val timerSpinnerList : MutableList<TimerSpinnerData>
) : ArrayAdapter<TimerSpinnerData>(context, resId, timerSpinnerList) {

    override fun getCount(): Int {
        return timerSpinnerList.size
    }

    override fun getItem(position: Int): TimerSpinnerData {
        return timerSpinnerList[position]
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = SpinnerTimerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val model = timerSpinnerList[position]
        try {
            binding.imgSpinner.setImageResource(model.themeImage)
            binding.themeText.text = model.theme
        }
        catch(e: Exception){
            e.printStackTrace()
        }
        return binding.root
    }
}
