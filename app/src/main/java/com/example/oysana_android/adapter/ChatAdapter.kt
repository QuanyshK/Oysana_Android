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

        private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        fun bind(msg: ChatMessageResponse) {
            // Скрываем оба контейнера
            binding.cardBot.visibility = View.GONE
            binding.cardUser.visibility = View.GONE

            // Парсим время из created_at
            val rawTime = msg.created_at
            val timeStr = if (rawTime.contains("T")) {
                rawTime.substringAfter("T").substringBefore(".").take(5)
            } else rawTime.takeIf { it.length >= 5 }?.take(5) ?: ""

            // Если у пользователя нет времени, подставляем текущее
            val displayTime = if (msg.localType == ChatMessageResponse.LocalType.USER && timeStr.isEmpty()) {
                LocalTime.now().format(timeFormatter)
            } else timeStr

            if (msg.localType == ChatMessageResponse.LocalType.BOT) {
                // Показываем ответ бота слева
                binding.cardBot.visibility = View.VISIBLE

                // Форматируем текст бота: убираем **, заменяем маркеры и добавляем отступы между строк
                val raw = msg.bot_response.orEmpty()
                val formatted = raw
                    .replace("**", "")
                    .replace("*   ", "• ")
                    .replace("\n", "\n\n")

                binding.textViewBotResponse.text = formatted
                binding.textViewTimestamp.text = displayTime

            } else {
                // Показываем сообщение пользователя справа
                binding.cardUser.visibility = View.VISIBLE
                binding.textViewUserMessage.text = msg.user_message
                binding.textViewTimestamp.text = displayTime
            }
        }
    }
}
