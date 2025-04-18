package com.example.oysana_android.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.oysana_android.R
import com.example.oysana_android.adapter.TopicAdapter
import com.example.oysana_android.data.model.Topic
import com.example.oysana_android.data.network.ApiClient
import com.example.oysana_android.data.network.ApiService
import com.example.oysana_android.databinding.FragmentTrialCourseDetailsBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearLayoutManager

class TrialCourseDetailsFragment : Fragment(R.layout.fragment_trial_course_details) {

    private var _binding: FragmentTrialCourseDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var apiService: ApiService
    private lateinit var topicAdapter: TopicAdapter

    private var courseId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTrialCourseDetailsBinding.bind(view)
        apiService = ApiClient.create()

        arguments?.let {
            courseId = it.getInt("courseId", -1)
        }

        setupRecyclerView()

        binding.btnBuySubscriptionTrial.setOnClickListener {
            val uri = Uri.parse(getString(R.string.web_home))
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        if (courseId != -1) {
            loadFirstTopics()
        } else {
            Toast.makeText(requireContext(), "Course ID табылмады", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        topicAdapter = TopicAdapter()
        binding.rvTopics.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = topicAdapter
        }
    }

    private fun loadFirstTopics() {
        lifecycleScope.launch {
            try {
                val response = apiService.getCourseFirstTopic(courseId)
                if (response.isSuccessful) {
                    val topics = response.body()
                    if (!topics.isNullOrEmpty()) {
                        topicAdapter.submitList(topics)
                        val firstVideo = topics.firstOrNull { it.videoUrl != null && it.isUnlocked }
                        setupYouTube(firstVideo?.videoUrl)
                    } else {
                        Toast.makeText(requireContext(), "Сабақтар табылмады", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Қате: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Қате: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupYouTube(videoUrl: String?) {
        videoUrl ?: return

        val youtubePlayerView: YouTubePlayerView = binding.youtubePlayerView
        lifecycle.addObserver(youtubePlayerView)

        val videoId = extractYoutubeVideoId(videoUrl)
        videoId?.let {
            youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(videoId, 0f)
                }
            })
        }
    }

    private fun extractYoutubeVideoId(url: String): String? {
        val uri = Uri.parse(url)
        return uri.getQueryParameter("v") ?: uri.lastPathSegment
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
