package com.example.oysana_android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.oysana_android.R
import com.example.oysana_android.data.database.AuthManager
import com.example.oysana_android.data.network.ApiClient

class ChatBotFragment : Fragment(R.layout.fragment_chat_bot) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val authManager = AuthManager(requireContext())
        if (authManager.isLoggedIn()) {
            // Показываем страницу чата (или вызываем загрузку данных)
            loadChatScreen()
        } else {
            // Переход на страницу логина
            findNavController().navigate(R.id.action_global_loginFragment)
        }

        // Здесь твоя логика чата
    }
    private fun loadChatScreen() {
        // Тут может быть инициализация UI, загрузка истории, подключение к сокету и т.п.
    }
}
