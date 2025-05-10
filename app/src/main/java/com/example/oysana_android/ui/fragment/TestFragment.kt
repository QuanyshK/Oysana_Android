// TestFragment.kt
package com.example.oysana_android.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.oysana_android.R
import com.example.oysana_android.adapter.TestAdapter
import com.example.oysana_android.data.database.AuthManager
import com.example.oysana_android.data.model.Answer
import com.example.oysana_android.data.model.Question
import com.example.oysana_android.data.network.AnswerDetail
import com.example.oysana_android.data.network.AnswerSubmission
import com.example.oysana_android.data.network.ApiClient
import com.example.oysana_android.data.network.ApiService
import com.example.oysana_android.data.network.SubmitTestRequest
import com.example.oysana_android.data.network.TestResultResponse
import com.example.oysana_android.databinding.FragmentTestBinding
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class TestFragment : Fragment(R.layout.fragment_test) {

    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!

    private lateinit var authManager: AuthManager
    private lateinit var apiService: ApiService

    private lateinit var questions: List<Question>
    private var answersMap: Map<Int, List<Answer>> = mapOf()
    private val userAnswers = mutableMapOf<Int, Int>()
    private var answerDetails: List<AnswerDetail> = listOf()

    private var topicId: Int = -1
    private var currentIndex = 0
    private var showResult = false
    private var testPassed = false
    private var testScore = 0

    private lateinit var adapter: TestAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentTestBinding.bind(view)
        authManager = AuthManager(requireContext())
        val token = authManager.getAccessToken()

        if (token.isNullOrEmpty()) {
            findNavController().navigate(R.id.action_global_loginFragment)
            return
        }

        apiService = ApiClient.create()

        topicId = arguments?.getInt("topicId", -1) ?: -1
        if (topicId == -1) {
            Toast.makeText(requireContext(), "Тақырып табылмады", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnFinish.setOnClickListener {
            submitTest()
        }

        loadTopic()
    }

    private fun loadTopic() {
        lifecycleScope.launch {
            try {
                val response = apiService.getTopicDetail(topicId)
                if (response.isSuccessful) {
                    val topic = response.body()
                    val test = topic?.test
                    if (test != null && test.questions.isNotEmpty()) {
                        questions = test.questions
                        answersMap = questions.associate { it.id to it.answers }
                        setupQuestionNav()
                        displayQuestion()
                    }
                } else {
                    Toast.makeText(requireContext(), "Қате: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Интернетке қосылыңыз", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupQuestionNav() {
        binding.questionNavContainer.removeAllViews()

        questions.forEachIndexed { index, question ->
            val button = MaterialButton(requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                text = "${index + 1}"
                setPadding(24, 12, 24, 12)
                strokeWidth = 2
                strokeColor = ContextCompat.getColorStateList(context, R.color.primary_blue)
                setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
                cornerRadius = 40

                when {
                    index == currentIndex -> {
                        setBackgroundColor(ContextCompat.getColor(context, R.color.primary_blue))
                        setTextColor(ContextCompat.getColor(context, android.R.color.white))
                    }
                    userAnswers.containsKey(question.id) -> {
                        setBackgroundColor(ContextCompat.getColor(context, R.color.background_light))
                        setTextColor(ContextCompat.getColor(context, android.R.color.black))
                    }
                    else -> {
                        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
                    }
                }

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(12, 0, 12, 0)
                }

                setOnClickListener {
                    currentIndex = index
                    displayQuestion()
                    setupQuestionNav()
                }
            }

            binding.questionNavContainer.addView(button)
        }
    }

    private fun displayQuestion() {
        val question = questions[currentIndex]
        val cleanText = question.text.replace(Regex("<[^>]*>"), "")
        binding.tvQuestionText.text = cleanText
        val answers = answersMap[question.id] ?: listOf()
        val selected = userAnswers[question.id]
        val correct = answerDetails.find { it.questionId == question.id }?.let {
            if (it.answeredCorrectly) selected else null
        }

        adapter = TestAdapter(
            answers,
            selected,
            showResult,
            correct
        ) { answerId ->
            userAnswers[question.id] = answerId
            binding.btnFinish.visibility = if (userAnswers.size == questions.size && !showResult) View.VISIBLE else View.GONE
        }

        binding.recyclerViewAnswers.adapter = adapter
        adapter.updateAnswers(answers)
    }

    private fun submitTest() {
        lifecycleScope.launch {
            try {
                val answerList = userAnswers.map { (questionId, answerId) ->
                    AnswerSubmission(questionId, answerId)
                }
                val request = SubmitTestRequest(answerList)
                val response = apiService.submitTest(topicId, request)

                if (response.isSuccessful) {
                    response.body()?.let {
                        showResult = true
                        testPassed = it.passed
                        testScore = it.score
                        answerDetails = it.answersDetail
                        Toast.makeText(requireContext(), "Сіз $testScore/${questions.size} жинадыңыз", Toast.LENGTH_LONG).show()
                        displayQuestion()
                    }
                } else {
                    Toast.makeText(requireContext(), "Қате: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Интернетке қосылыңыз", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
