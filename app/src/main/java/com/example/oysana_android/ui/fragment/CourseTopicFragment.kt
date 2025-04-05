package com.example.oysana_android.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.oysana_android.R
import com.example.oysana_android.data.database.AuthManager
import com.example.oysana_android.data.model.Topic
import com.example.oysana_android.data.network.ApiClient
import com.example.oysana_android.data.network.ApiService
import com.example.oysana_android.databinding.FragmentCourseTopicBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch

class CourseTopicFragment : Fragment(R.layout.fragment_course_topic) {

    private var _binding: FragmentCourseTopicBinding? = null
    private val binding get() = _binding!!

    private lateinit var authManager: AuthManager
    private lateinit var apiService: ApiService

    private var topicId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentCourseTopicBinding.bind(view)

        authManager = AuthManager(requireContext())
        val token = authManager.getAccessToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Қайта кіріңіз", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_global_loginFragment)
            return
        }

        apiService = ApiClient.create(token)

        topicId = arguments?.getInt("topicId", -1) ?: -1
        if (topicId != -1) {
            loadTopicDetails()
        } else {
            Toast.makeText(requireContext(), "Тақырып табылмады", Toast.LENGTH_SHORT).show()
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnStartTest.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("topicId", topicId)
            }
            findNavController().navigate(R.id.action_courseTopicFragment_to_testFragment, bundle)
        }
    }

    private fun loadTopicDetails() {
        lifecycleScope.launch {
            try {
                val response = apiService.getTopicDetail(topicId)
                if (response.isSuccessful) {
                    val topic = response.body()
                    topic?.let {
                        setupYouTube(it.videoUrl)
                        val testResult = it.userResult!!.score.toString()
                        binding.tvTestResult.text =
                            " Cіздің балыңыз: $testResult"
                    }
                } else {
                    Toast.makeText(requireContext(), "Қате: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun setupYouTube(videoUrl: String?) {
        videoUrl ?: return
        val videoId = extractYoutubeVideoId(videoUrl)
        val youtubePlayerView: YouTubePlayerView = binding.youtubePlayerView
        lifecycle.addObserver(youtubePlayerView)

        videoId?.let {
            youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(player: YouTubePlayer) {
                    player.cueVideo(it, 0f)
                }
            })
        }
    }

    private fun extractYoutubeVideoId(url: String): String? {
        val uri = android.net.Uri.parse(url)
        return uri.getQueryParameter("v") ?: uri.lastPathSegment
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
