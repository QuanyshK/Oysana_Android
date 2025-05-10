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
import com.example.oysana_android.data.database.AuthManager
import com.example.oysana_android.data.model.Course
import com.example.oysana_android.data.network.ApiClient
import com.example.oysana_android.data.network.ApiService
import com.example.oysana_android.databinding.FragmentMyCoursesBinding
import kotlinx.coroutines.launch

class MyCoursesFragment : Fragment(R.layout.fragment_my_courses) {

    private var _binding: FragmentMyCoursesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CourseAdapter
    private lateinit var authManager: AuthManager
    private lateinit var apiService: ApiService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMyCoursesBinding.bind(view)

        authManager = AuthManager(requireContext())
        val token = authManager.getAccessToken()
        apiService = ApiClient.create()
        setupRecyclerView()

        if (authManager.isLoggedIn()) {
            loadMyCourses()
        } else {
            findNavController().navigate(R.id.action_global_loginFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = CourseAdapter { course ->
            val bundle = Bundle().apply {
                putInt("courseId", course.id)
                putString("courseTitle", course.title)
            }
            findNavController().navigate(R.id.action_myCoursesFragment_to_courseDetailsFragment, bundle)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun loadMyCourses() {
        lifecycleScope.launch {
            try {
                val response = apiService.getMyCourses()
                if (response.isSuccessful) {
                    val courses = response.body()
                    if (!courses.isNullOrEmpty()) {
                        adapter.submitList(courses)
                    }
                } else { }
            } catch (e: Exception) {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
