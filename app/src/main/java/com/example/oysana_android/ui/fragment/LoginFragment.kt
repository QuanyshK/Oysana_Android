package com.example.oysana_android.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.oysana_android.R
import com.example.oysana_android.data.database.AuthManager
import com.example.oysana_android.data.network.*
import com.example.oysana_android.databinding.FragmentLoginBinding
import kotlinx.coroutines.*
import retrofit2.HttpException

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authManager: AuthManager
    private lateinit var apiService: ApiService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        authManager = AuthManager(requireContext())
        apiService = ApiClient.create()

        binding.etPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Логин және құпиясөзді толтырыңыз", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(username, password)
            }
        }

        binding.tvInstagramLink.setOnClickListener {
            val uri = Uri.parse(getString(R.string.insta_link))
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        binding.btnBuySubscriptionLogin.setOnClickListener {
            val uri = Uri.parse(getString(R.string.web_home))
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        binding.btnSupport.setOnClickListener {
            val uri = Uri.parse(getString(R.string.support))
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    private fun loginUser(username: String, password: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.login(LoginRequest(username, password))
                }

                if (response.isSuccessful && response.body() != null) {
                    val tokenResponse = response.body()!!

                    authManager.saveTokens(tokenResponse.access, tokenResponse.refresh)

                    authManager.saveUsername(username)

                    createUserPost(username)

                    Toast.makeText(requireContext(), "Қош келдіңіз, $username!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(
                        R.id.action_loginFragment_to_profileFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.nav_graph, true)  // Очистить стек
                            .setLaunchSingleTop(true)
                            .build()
                    )                } else {
                    handleLoginError(response.code())
                }
            } catch (e: HttpException) {
                Toast.makeText(requireContext(), "Сервер қатесі", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Интернетке қосылыңыз", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLoginError(code: Int) {
        when (code) {
            400, 401 -> Toast.makeText(requireContext(), "Логин немесе құпия сөз жарамсыз", Toast.LENGTH_SHORT).show()
            404 -> Toast.makeText(requireContext(), "Сервер табылмады (404)", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(requireContext(), "Қате: $code", Toast.LENGTH_SHORT).show()
        }
    }
    private fun createUserPost(username: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val aiService = AIClient.create(requireContext())
                val response = aiService.createUser(mapOf("username" to username))

                if (response.isSuccessful) {
                    println("✅ Юзер создан или уже существует")
                } else {
                    println("❌ Ошибка при создании: ${response.code()}")
                }
            } catch (e: Exception) {
                println("❌ Ошибка соединения: ${e.localizedMessage}")
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
