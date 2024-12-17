package com.example.catalogsvg

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.catalogsvg.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        val navController = findNavController()
        binding.btnYes.setOnClickListener {
            saveAnswer(requireContext(), true)
            navController.navigate(R.id.action_settingsFragment_to_catalogFragment)
        }
        binding.btnNo.setOnClickListener {
            saveAnswer(requireContext(), false)
            navController.navigate(R.id.action_settingsFragment_to_catalogFragment)
        }
        binding.imgBtnOpenVk.setOnClickListener {
            openVkPage()
        }
        binding.imgBtnOpenVisio.setOnClickListener {
            openVisioPage()
        }
        binding.imgBtnChangeProfile.setOnClickListener {
            navController.navigate(R.id.action_settingsFragment_to_mainFragment)
        }
        return view
    }

    private fun openVkPage() {
        val url = "https://vk.com/club228624863"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
    private fun openVisioPage() {
        val url = "https://drive.google.com/file/d/1Tz74xqopfXlKqdyOmkZCP_uGYbthn2wX/view?usp=drive_link"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    fun saveAnswer(context: Context, answer: Boolean) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("answer", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("answer", answer)
        editor.apply()
    }
}