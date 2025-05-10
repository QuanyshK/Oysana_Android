package com.example.oysana_android.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.oysana_android.R
import com.example.oysana_android.data.database.AuthManager
import com.example.oysana_android.data.model.ChatCreateRequest
import com.example.oysana_android.data.model.ChatMessageResponse
import com.example.oysana_android.data.network.AIClient
import com.example.oysana_android.data.network.AIService
import com.example.oysana_android.databinding.FragmentChatBotBinding
import com.example.oysana_android.adapter.ChatAdapter
import kotlinx.coroutines.launch

class ChatBotFragment : Fragment(R.layout.fragment_chat_bot) {

    private var _binding: FragmentChatBotBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessageResponse>()
    private lateinit var aiService: AIService
    private lateinit var authManager: AuthManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChatBotBinding.bind(view)

        authManager = AuthManager(requireContext())

        if (!authManager.isLoggedIn()) {
            findNavController().navigate(R.id.action_global_loginFragment)
            return
        }

        aiService = AIClient.create(requireContext())

        setupRecyclerView()
        setupSendButton()
        loadChatMessages()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply { stackFromEnd = true }
            adapter = chatAdapter
        }
    }

    private fun setupSendButton() {
        binding.buttonSend.setOnClickListener {
            val message = binding.editTextMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                binding.editTextMessage.setText("")
                sendChatMessage(message)
            }
        }
    }

    private fun loadChatMessages() {
        lifecycleScope.launch {
            try {
                val response = aiService.getChats()
                if (response.isSuccessful) {
                    messages.clear()

                    response.body()?.forEach { msg ->
                        if (msg.user_message.isNotEmpty()) {
                            messages.add(
                                ChatMessageResponse(
                                    id = msg.id,
                                    user_message = msg.user_message,
                                    bot_response = null,
                                    created_at = msg.created_at
                                ).apply {
                                    localType = ChatMessageResponse.LocalType.USER
                                }
                            )
                        }

                        if (!msg.bot_response.isNullOrEmpty()) {
                            messages.add(
                                ChatMessageResponse(
                                    id = msg.id,
                                    user_message = "",
                                    bot_response = msg.bot_response,
                                    created_at = msg.created_at
                                ).apply {
                                    localType = ChatMessageResponse.LocalType.BOT
                                }
                            )
                        }
                    }

                    chatAdapter.submitList(messages.toList())
                    scrollToBottom()
                } else {
                }
            } catch (e: Exception) {
            }
        }
    }


    private fun sendChatMessage(message: String) {
        val userMessage = ChatMessageResponse(
            user_message = message,
            bot_response = null,
            created_at = ""
        ).apply {
            localType = ChatMessageResponse.LocalType.USER
        }

        messages.add(userMessage)
        chatAdapter.submitList(messages.toList())
        scrollToBottom()

        lifecycleScope.launch {
            try {
                val response = aiService.createChat(ChatCreateRequest(message))
                if (response.isSuccessful) {
                    response.body()?.let { botMessage ->
                        botMessage.localType = ChatMessageResponse.LocalType.BOT
                        messages.add(botMessage)
                        chatAdapter.submitList(messages.toList())
                        scrollToBottom()
                    }
                } else {
                }
            } catch (e: Exception) {
                showError("Жіберу қатесі")
            }
        }
    }


    private fun scrollToBottom() {
        _binding?.recyclerViewChat?.postDelayed({
            _binding?.recyclerViewChat?.scrollToPosition(messages.size - 1)
        }, 100)
    }


    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
