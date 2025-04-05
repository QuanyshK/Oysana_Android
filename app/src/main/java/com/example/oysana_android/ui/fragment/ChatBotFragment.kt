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
        if (!authManager.isLoggedIn()) {
            findNavController().navigate(R.id.action_global_loginFragment)
            return
        }

        // Здесь твоя логика чата
    }
}
