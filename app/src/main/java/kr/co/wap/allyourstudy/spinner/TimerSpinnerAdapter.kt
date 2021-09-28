package kr.co.wap.allyourstudy.spinner

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import kr.co.wap.allyourstudy.R
import kr.co.wap.allyourstudy.databinding.SpinnerTimerItemBinding
import java.lang.Exception

class TimerSpinnerAdapter(context: Context,
                          @LayoutRes private val resId: Int,
                          private val timerSpinnerList : MutableList<TimerSpinnerData>
) : ArrayAdapter<TimerSpinnerData>(context, resId, timerSpinnerList) {

    override fun getCount(): Int {
        return timerSpinnerList.size
    }

    override fun getItem(position: Int): TimerSpinnerData {
        return timerSpinnerList[position]
    }
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View{
        val binding = SpinnerTimerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val model = timerSpinnerList[position]
        try{
            binding.themeText.text = model.theme
            binding.themeText.setTextColor(ContextCompat.getColor(context, R.color.white))
            binding.imgSpinner.setColorFilter(ContextCompat.getColor(context, R.color.white))
            binding.imgSpinner.setImageResource(model.themeImage)
        }
        catch(e: Exception){
            e.printStackTrace()
        }
        return binding.root
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
