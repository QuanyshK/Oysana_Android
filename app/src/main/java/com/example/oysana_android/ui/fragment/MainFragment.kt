package com.example.oysana_android.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.oysana_android.R
import com.example.oysana_android.data.model.Course
import com.example.oysana_android.data.network.ApiClient
import com.example.oysana_android.data.network.ApiService
import com.example.oysana_android.databinding.FragmentMainBinding
import com.example.oysana_android.adapter.CourseAdapter
import kotlinx.coroutines.launch

class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var apiService: ApiService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentMainBinding.bind(view)

        apiService = ApiClient.create()


        setupRecyclerView()
        loadCourses()

        binding.btnBuySubscription.setOnClickListener {
            findNavController().navigate(R.id.registrationFragment)
        }
    }

    private fun setupRecyclerView() {
        courseAdapter = CourseAdapter { course ->
            val bundle = Bundle().apply {
                putInt("courseId", course.id)
            }
            findNavController().navigate(
                R.id.action_mainFragment_to_trialCourseDetailsFragment,
                bundle
            )
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = courseAdapter
    }


    private fun loadCourses() {
        lifecycleScope.launch {
            try {
                val response = apiService.getPublicCourses()
                if (response.isSuccessful) {
                    response.body()?.let {
                        courseAdapter.submitList(it)
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
