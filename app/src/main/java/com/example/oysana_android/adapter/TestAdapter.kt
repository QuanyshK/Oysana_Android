package com.example.oysana_android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.oysana_android.data.model.Answer
import com.example.oysana_android.databinding.ItemTestBinding

class TestAdapter(
    private var answers: List<Answer>,
    private val selectedAnswerId: Int?,
    private val isResultVisible: Boolean,
    private val correctAnswerId: Int?,
    private val onAnswerSelected: (Int) -> Unit
) : RecyclerView.Adapter<TestAdapter.AnswerViewHolder>() {

    inner class AnswerViewHolder(private val binding: ItemTestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: Answer) {
            binding.radioAnswer.text = answer.text
            binding.radioAnswer.isChecked = answer.id == selectedAnswerId
            binding.radioAnswer.isEnabled = !isResultVisible

            if (isResultVisible) {
                binding.radioAnswer.setBackgroundResource(
                    when {
                        answer.id == correctAnswerId && answer.id == selectedAnswerId -> android.R.color.holo_green_light
                        answer.id == selectedAnswerId && answer.id != correctAnswerId -> android.R.color.holo_red_light
                        else -> android.R.color.transparent
                    }
                )
            } else {
                binding.radioAnswer.setBackgroundResource(android.R.color.transparent)
            }

            binding.radioAnswer.setOnClickListener {
                onAnswerSelected(answer.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val binding = ItemTestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnswerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(answers[position])
    }

    override fun getItemCount() = answers.size

    fun updateAnswers(newAnswers: List<Answer>) {
        answers = newAnswers
        notifyDataSetChanged()
    }
}
