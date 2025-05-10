package com.example.oysana_android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.oysana_android.data.model.ChatMessageResponse
import com.example.oysana_android.databinding.ItemMessageUserBinding
import com.example.oysana_android.databinding.ItemMessageBotBinding

class ChatAdapter :
    ListAdapter<ChatMessageResponse, RecyclerView.ViewHolder>(ChatDiffCallback()) {

    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_BOT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).localType ?: ChatMessageResponse.LocalType.BOT) {
            ChatMessageResponse.LocalType.USER -> TYPE_USER
            ChatMessageResponse.LocalType.BOT -> TYPE_BOT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_USER -> {
                val binding = ItemMessageUserBinding.inflate(inflater, parent, false)
                UserMessageViewHolder(binding)
            }
            TYPE_BOT -> {
                val binding = ItemMessageBotBinding.inflate(inflater, parent, false)
                BotMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is UserMessageViewHolder -> holder.bind(message)
            is BotMessageViewHolder -> holder.bind(message)
        }
    }

    class UserMessageViewHolder(private val binding: ItemMessageUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessageResponse) {
            binding.textViewUserMessage.text = message.user_message
        }
    }

    class BotMessageViewHolder(private val binding: ItemMessageBotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessageResponse) {
            val raw = message.bot_response ?: ""
            val formatted = raw
                .replace("**", "")
                .replace("*   ", "• ")
                .replace("\n", "\n\n")

            binding.textViewBotResponse.text = formatted

        }
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessageResponse>() {
    override fun areItemsTheSame(oldItem: ChatMessageResponse, newItem: ChatMessageResponse): Boolean {
        return oldItem.id == newItem.id && oldItem.created_at == newItem.created_at
    }

    override fun areContentsTheSame(oldItem: ChatMessageResponse, newItem: ChatMessageResponse): Boolean {
        return oldItem == newItem
    }
}