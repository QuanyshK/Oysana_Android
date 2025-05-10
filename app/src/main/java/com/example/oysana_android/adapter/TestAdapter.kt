package com.example.oysana_android.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.oysana_android.R
import com.example.oysana_android.data.model.Answer
import com.example.oysana_android.databinding.ItemTestBinding

class TestAdapter(
    private var answers: List<Answer>,
    private var selectedAnswerId: Int?,
    private val isResultVisible: Boolean,
    private val correctAnswerId: Int?,
    private val onAnswerSelected: (Int) -> Unit
) : RecyclerView.Adapter<TestAdapter.AnswerViewHolder>() {

    inner class AnswerViewHolder(private val binding: ItemTestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(answer: Answer) {
            val cleanText = answer.text.replace(Regex("<[^>]*>"), "")
            binding.radioAnswer.text = cleanText
            binding.radioAnswer.isChecked = answer.id == selectedAnswerId
            binding.radioAnswer.isEnabled = !isResultVisible

            val context = binding.radioAnswer.context

            if (isResultVisible) {
                when {
                    answer.id == correctAnswerId && answer.id == selectedAnswerId -> {
                        binding.radioAnswer.setBackgroundColor(ContextCompat.getColor(context, R.color.correct_green))
                    }
                    answer.id == selectedAnswerId && answer.id != correctAnswerId -> {
                        binding.radioAnswer.setBackgroundColor(ContextCompat.getColor(context, R.color.wrong_red))
                    }
                    answer.id == correctAnswerId -> {
                        binding.radioAnswer.setBackgroundColor(ContextCompat.getColor(context, R.color.correct_green))
                    }
                    else -> {
                        binding.radioAnswer.setBackgroundColor(Color.TRANSPARENT)
                    }
                }
            } else {
                binding.radioAnswer.setBackgroundColor(Color.TRANSPARENT)
            }

            binding.radioAnswer.setOnClickListener {
                if (!isResultVisible) {
                    selectedAnswerId = answer.id
                    onAnswerSelected(answer.id)
                    notifyDataSetChanged()
                }
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

    override fun getItemCount(): Int = answers.size

    fun updateAnswers(newAnswers: List<Answer>) {
        answers = newAnswers
        notifyDataSetChanged()
    }
}
