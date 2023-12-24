package com.example.telehealth.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.R
import com.example.telehealth.data.dataclass.DoctorModel

interface OnDeleteListener {
    fun onDeleteDoctor(doctor: DoctorModel)
}
class DoctorAdapterAdmin(private var doctors: List<DoctorModel>, private val onDeleteListener: OnDeleteListener): RecyclerView.Adapter<DoctorAdapterAdmin.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("doctors 99", "doctor at adapter ${doctors.toString()}")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem=doctors[position]

        val doctorDetails="${currentItem.doctorName}, ${currentItem.specialty}"
        holder.textView.text=doctorDetails

        holder.btnDelete.setOnClickListener {
            onDeleteListener.onDeleteDoctor(currentItem)
        }
    }

    fun updateList(newList: MutableList<DoctorModel>) {
        doctors = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = doctors.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.doctorDetails)
        val btnDelete: Button = view.findViewById(R.id.doctorDeleteBtn)
    }
}