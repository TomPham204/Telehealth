package com.example.telehealth.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.databinding.ItemMessageBinding

data class MessageDisplayModel (val sender: String, val text: String)


class MessageAdapter(private val messages: MutableList<MessageDisplayModel>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageDisplayModel) {
            binding.textSender.text = message.sender
            binding.textViewMessage.text = message.text

            if(message.sender=="Me") {
                binding.textSender.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                binding.textViewMessage.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
            } else {
                // Optionally, you can define the alignment for the "Other" case
                binding.textSender.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                binding.textViewMessage.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMessageBinding.inflate(inflater, parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    fun updateMessages(newMessages: List<MessageDisplayModel>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = messages.size
}

