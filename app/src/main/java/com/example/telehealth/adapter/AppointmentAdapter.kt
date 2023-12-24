package com.example.telehealth.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.R
import com.example.telehealth.data.dataclass.AppointmentModel

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

        holder.buttonRemove.setOnClickListener {
            onRemoveClickListener.onRemoveClick(currentItem)
        }
        holder.buttonVideoCall.setOnClickListener {
            onVideoCallClickListener.onVideoCallClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    fun updateList(newList: MutableList<AppointmentModel>) {
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

