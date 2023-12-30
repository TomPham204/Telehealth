package com.example.telehealth.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.R
import com.example.telehealth.data.dataclass.AppointmentModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

interface OnRemoveClickListener {
    fun onRemoveClick(appointment: AppointmentModel)
}

interface OnVideoCallClickListener {
    fun onVideoCallClick(appointment: AppointmentModel)
}
class AppointmentAdapter(
    private var appointments: MutableList<AppointmentModel>,
    private val onRemoveClickListener: OnRemoveClickListener,
    private val onVideoCallClickListener: OnVideoCallClickListener
) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.appointment_item, parent, false)
        return AppointmentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val currentItem = appointments[position]
        holder.doctorName.text = currentItem.doctorName.toString()
        holder.time.text = currentItem.dateTime.toString()
        holder.status.text = "Status: ${currentItem.status}"

        holder.buttonRemove.setOnClickListener {
            onRemoveClickListener.onRemoveClick(currentItem)
        }
        holder.buttonVideoCall.setOnClickListener {
            onVideoCallClickListener.onVideoCallClick(currentItem)
        }

        // Parse the date and time from the string
        val appointmentDateTime = parseDate(currentItem.dateTime.toString())
        val currentTime = Calendar.getInstance().time

        // Check if current time is within ±10 minutes of appointment time
        holder.buttonVideoCall.isEnabled =
            appointmentDateTime != null && isWithinTimeRange(appointmentDateTime, currentTime) && currentItem.status=="ACCEPTED"
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    fun updateList(newList: MutableList<AppointmentModel>) {
        appointments = newList
        notifyDataSetChanged()
    }

    private fun parseDate(dateStr: String): Date? {
        return try {
            val format = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.getDefault())
            format.parse(dateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    private fun isWithinTimeRange(appointmentTime: Date, currentTime: Date): Boolean {
        val tenMinutesInMillis = 10 * 60 * 1000 // 10 minutes in milliseconds
        return (currentTime.time > appointmentTime.time - tenMinutesInMillis) &&
                (currentTime.time < appointmentTime.time + tenMinutesInMillis)
    }

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorName: TextView = itemView.findViewById(R.id.textDoctorName)
        val time: TextView = itemView.findViewById(R.id.textTime)
        val status: TextView = itemView.findViewById(R.id.textStatus)
        val buttonRemove: Button = itemView.findViewById(R.id.buttonRemove)
        val buttonVideoCall: Button = itemView.findViewById(R.id.buttonVideoCall)
    }
}

