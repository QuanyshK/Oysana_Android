package com.example.oysana_android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.oysana_android.data.model.ChatMessageResponse
import com.example.oysana_android.databinding.ItemMessageBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatVH>() {

    private val items = mutableListOf<ChatMessageResponse>()

    fun submitList(newList: List<ChatMessageResponse>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatVH {
        val binding = ItemMessageBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatVH(binding)
    }

    override fun onBindViewHolder(holder: ChatVH, position: Int) {
        holder.bind(items[position])
    }

    inner class ChatVH(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(msg: ChatMessageResponse) {
            binding.cardBot.visibility = View.GONE
            binding.cardUser.visibility = View.GONE



            if (msg.localType == ChatMessageResponse.LocalType.BOT) {
                binding.cardBot.visibility = View.VISIBLE

                val raw = msg.bot_response.orEmpty()
                val formatted = raw
                    .replace("**", "")
                    .replace("*   ", "• ")
                    .replace("\n", "\n\n")

                binding.textViewBotResponse.text = formatted

            } else {
                binding.cardUser.visibility = View.VISIBLE
                binding.textViewUserMessage.text = msg.user_message
            }
        }
    }
}
