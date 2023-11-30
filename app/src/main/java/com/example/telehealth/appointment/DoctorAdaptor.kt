package com.example.telehealth.appointment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.telehealth.R

data class Doctor(
    var name: String,
    var specialty: String,
    var workplace: String,
    var id: Int,
)

data class DoctorSpinnerItem(
    val displayText: String,
    val doctor: Doctor
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

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}
