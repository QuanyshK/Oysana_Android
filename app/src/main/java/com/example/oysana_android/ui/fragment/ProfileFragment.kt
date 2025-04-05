package com.example.oysana_android.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.oysana_android.BuildConfig
import com.example.oysana_android.R
import com.example.oysana_android.data.database.AuthManager
import com.example.oysana_android.data.network.ApiClient
import com.example.oysana_android.data.network.ApiService
import com.example.oysana_android.databinding.FragmentProfileBinding
import kotlinx.coroutines.*
import retrofit2.HttpException

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var authManager: AuthManager
    private lateinit var apiService: ApiService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentProfileBinding.bind(view)

        authManager = AuthManager(requireContext())
        val token = authManager.getAccessToken()
        apiService = ApiClient.create(token)

        binding.btnLogoutSmall.setOnClickListener {
            authManager.clearTokens()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        binding.btnSupport.setOnClickListener {
            val uri = Uri.parse(getString(R.string.support))
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        binding.btnTrialTest.setOnClickListener {
            val uri = Uri.parse(getString(R.string.trial_test))
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        binding.tvAppVersion.text = "⭐ Version ${BuildConfig.VERSION_NAME}"

        loadUserInfo()
    }

    private fun loadUserInfo() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) { apiService.getCurrentUser() }
                if (response.isSuccessful) {
                    val user = response.body()
                    binding.tvUserName.text = user?.name ?: "Аты белгісіз"
                }
            } catch (e: HttpException) {
                showToast("Сервер қатесі")
            } catch (e: Exception) {
                showToast("Интернетке қосылыңыз")
            }
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
