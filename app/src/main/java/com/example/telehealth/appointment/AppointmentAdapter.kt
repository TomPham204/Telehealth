package com.example.telehealth.appointment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

interface OnRemoveClickListener {
    fun onRemoveClick(appointment: Appointment)
}

interface OnVideoCallClickListener {
    fun onVideoCallClick(appointment: Appointment)
}
class AppointmentAdapter(
    private var appointments: MutableList<Appointment>,
    private val onRemoveClickListener: OnRemoveClickListener,
    private val onVideoCallClickListener: OnVideoCallClickListener) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.appointment_item, parent, false)
        return AppointmentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val currentItem = appointments[position]
        holder.doctorName.text = currentItem.doctorName
        holder.time.text = convertTimestampToDateString(currentItem.time.toLong())

        holder.buttonRemove.setOnClickListener {
            onRemoveClickListener.onRemoveClick(currentItem)
        }
        holder.buttonVideoCall.setOnClickListener {
            onVideoCallClickListener.onVideoCallClick(currentItem)
        }
    }

    private fun convertTimestampToDateString(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp // Set calendar time using timestamp
        return sdf.format(calendar.time)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    fun updateList(newList: MutableList<Appointment>) {
        appointments = newList
        notifyDataSetChanged()
    }

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorName: TextView = itemView.findViewById(R.id.textDoctorName)
        val time: TextView = itemView.findViewById(R.id.textTime)
        val buttonRemove: Button = itemView.findViewById(R.id.buttonRemove)
        val buttonVideoCall: Button = itemView.findViewById(R.id.buttonVideoCall)
    }
}

