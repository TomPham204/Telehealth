package com.example.telehealth.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.R
import com.example.telehealth.data.dataclass.AppointmentModel
import java.text.SimpleDateFormat
import java.util.Locale

interface OnAcceptListener {
    fun onAccept(appointment: AppointmentModel)
}

interface OnRejectListener {
    fun onReject(appointment: AppointmentModel)
}

class AppointmentAdapterAdmin(private var appointments: List<AppointmentModel>, private val onAcceptListener: OnAcceptListener, private val onRejectListener: OnRejectListener): RecyclerView.Adapter<AppointmentAdapterAdmin.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = appointments[position]

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedDateTime = formatter.format(currentItem.dateTime)

        // Combine the details into a string
        val appointmentDetails = "ID: ${currentItem.appointmentId}\n" +
                "Doctor: ${currentItem.doctorName}\n" +
                "Date & Time: $formattedDateTime\n" +
                "Status: ${currentItem.status}"

        holder.textView.text=appointmentDetails
        holder.btnAccept.setOnClickListener {
            // Logic for accept action
            onAcceptListener.onAccept(currentItem)
        }
        holder.btnReject.setOnClickListener {
            // Logic for reject action
            onRejectListener.onReject(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    fun updateList(newList: MutableList<AppointmentModel>) {
        appointments = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tvAppointmentDetails)
        val btnAccept: Button = view.findViewById(R.id.btnAccept)
        val btnReject: Button = view.findViewById(R.id.btnReject)
    }
}