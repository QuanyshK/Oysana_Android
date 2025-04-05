package com.example.oysana_android.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.example.oysana_android.R
import com.example.oysana_android.data.model.Topic

class TopicAdapter(
    private val onItemClick: ((Topic) -> Unit)? = null
) : ListAdapter<Topic, TopicAdapter.TopicViewHolder>(DiffCallback()) {

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(topic: Topic) {
            val title = itemView.findViewById<TextView>(R.id.tvTopicTitle)
            val locked = itemView.findViewById<TextView>(R.id.tvLockedStatus)

            title.text = topic.title
            locked.visibility = if (topic.isUnlocked) View.GONE else View.VISIBLE

            itemView.setOnClickListener {
                if (topic.isUnlocked) onItemClick?.let { it1 -> it1(topic) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_topic, parent, false)
        return TopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Topic>() {
        override fun areItemsTheSame(oldItem: Topic, newItem: Topic) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Topic, newItem: Topic) = oldItem == newItem
    }
}


