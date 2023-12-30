package com.example.telehealth.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.telehealth.R
import com.example.telehealth.data.dataclass.DoctorModel

data class DoctorSpinnerItem(
    val displayText: String,
    val doctor: DoctorModel
)

class DoctorAdapter(context: Context, doctors: List<DoctorSpinnerItem>) :
    ArrayAdapter<DoctorSpinnerItem>(context, R.layout.doctor_spinner_item, doctors) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.doctor_spinner_item,
            parent,
            false
        )

        val doctor = getItem(position)
        val textView = view.findViewById<TextView>(R.id.spinnerItem)
        textView.text = doctor?.displayText

        return view
    }

    fun updateList(newList: List<DoctorSpinnerItem>) {
        clear()
        addAll(newList)
        notifyDataSetChanged()
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}
