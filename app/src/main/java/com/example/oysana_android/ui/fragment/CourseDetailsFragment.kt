package com.example.oysana_android.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.oysana_android.R
import com.example.oysana_android.adapter.CourseAdapter
import com.example.oysana_android.adapter.TopicAdapter
import com.example.oysana_android.data.database.AuthManager
import com.example.oysana_android.data.model.Topic
import com.example.oysana_android.data.network.ApiClient
import com.example.oysana_android.databinding.FragmentCourseDetailsBinding
import kotlinx.coroutines.launch

class CourseDetailsFragment : Fragment(R.layout.fragment_course_details) {

    private var _binding: FragmentCourseDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var topicAdapter: TopicAdapter
    private var courseId: Int = -1
    private lateinit var authManager: AuthManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCourseDetailsBinding.bind(view)
        authManager = AuthManager(requireContext())
        arguments?.let {
            courseId = it.getInt("courseId", -1)
        }

        if (courseId == -1) {
            Toast.makeText(requireContext(), "Курс табылмады", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        setupRecyclerView()
        loadCourseTopics()
        val courseTitle = arguments?.getString("courseTitle", "Курс атауы")
        binding.tvCourseTitle.text = courseTitle


        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        topicAdapter = TopicAdapter { topic ->
            val bundle = Bundle().apply {
                putInt("topicId", topic.id)
                putString("topicTitle", topic.title)
            }
            findNavController().navigate(
                R.id.action_courseDetailsFragment_to_courseTopicFragment,
                bundle
            )
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = topicAdapter
    }

    private fun loadCourseTopics() {
        lifecycleScope.launch {
            try {
                val token = authManager.getAccessToken()
                val response = ApiClient.create().getCourseTopics(courseId)
                if (response.isSuccessful) {
                    response.body()?.let { topicList ->
                        topicAdapter.submitList(topicList)
                    }
                } else {
                }
            } catch (e: Exception) {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
