package com.example.oysana_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.oysana_android.R
import com.example.oysana_android.data.model.RegistrationRequest
import com.example.oysana_android.data.network.ApiClient
import com.example.oysana_android.data.network.ApiService
import com.example.oysana_android.databinding.FragmentRegistrationBinding
import kotlinx.coroutines.launch

class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private lateinit var apiService: ApiService
    private lateinit var subjectPairs: List<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentRegistrationBinding.bind(view)
        apiService = ApiClient.create()

        subjectPairs = listOf(
            "Дүниежүзілік тарих және География",
            "Дүниежүзілік тарих және Құқық негіздері",
            "География және Шет тілі",
            "Биология және География",
            "Шет тілі және Дүниежүзілік тарих",
            "Қазақ тілі және Қазақ әдебиеті",
            "Математика және География",
            "Математика және Информатика",
            "Математика және Физика",
            "Русский язык и Русская литература",
            "Химия және Биология",
            "Химия және Физика"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjectPairs)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPair.adapter = adapter

        binding.btnSubmit.setOnClickListener {
            val name = binding.inputName.text.toString().trim()
            val phone = binding.inputPhone.text.toString().trim()
            val pair = binding.spinnerPair.selectedItem?.toString()?.trim().orEmpty()

            if (name.isEmpty() || phone.isEmpty() || pair.isEmpty()) {
                Toast.makeText(requireContext(), "Барлық өрістерді толтырыңыз", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = RegistrationRequest(name, phone, pair)
            lifecycleScope.launch {
                try {
                    val response = apiService.registerLead(request)
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Өтініш қабылданды!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Қате: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Интернетке қосылыңыз", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
