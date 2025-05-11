package com.example.oysana_android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.oysana_android.data.model.ChatMessageResponse
import com.example.oysana_android.databinding.ItemMessageBinding

class ChatAdapter :
    ListAdapter<ChatMessageResponse, ChatAdapter.ChatVH>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatVH {
        val binding = ItemMessageBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatVH(binding)
    }

    override fun onBindViewHolder(holder: ChatVH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChatVH(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(msg: ChatMessageResponse) {
            binding.cardBot.visibility = View.GONE
            binding.cardUser.visibility = View.GONE

            if (msg.localType == ChatMessageResponse.LocalType.BOT) {
                binding.cardBot.visibility = View.VISIBLE
                val formatted = msg.bot_response.orEmpty()
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

class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessageResponse>() {
    override fun areItemsTheSame(oldItem: ChatMessageResponse, newItem: ChatMessageResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatMessageResponse, newItem: ChatMessageResponse): Boolean {
        return oldItem == newItem
    }
}
