package com.example.telehealth.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.telehealth.R
import com.example.telehealth.data.dataclass.ProfileModel

data class PatientSpinnerItem(
    val displayText: String,
    val patient: ProfileModel
)

class PatientAdapter(context: Context, private var patients: List<PatientSpinnerItem>) :
    ArrayAdapter<PatientSpinnerItem>(context, R.layout.chat_spinner_item, patients) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.chat_spinner_item,
            parent,
            false
        )

        val patient = getItem(position)
        val textView = view.findViewById<TextView>(R.id.spinnerItem)
        textView.text = patient?.displayText

        return view
    }

    fun updateList(newList: List<PatientSpinnerItem>) {
        clear()
        addAll(newList)
        notifyDataSetChanged()
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}
