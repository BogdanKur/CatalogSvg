package com.example.catalogsvg

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.catalogsvg.databinding.FragmentMainBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    lateinit var users: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater,container, false)
        val view = binding.root
        val navController = findNavController()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1001)
        }
        users = FirebaseDatabase.getInstance().getReference("users")
        binding.btnAuth.setOnClickListener {
            if(binding.etLoginEnter.text.isNotEmpty() && binding.etPasswordEnter.text.isNotEmpty()) {
                users.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(snap in snapshot.children) {
                            Log.e("login", snap.child("password").getValue<String?>().toString())
                            if(snap.child("login").getValue<String?>().toString() == binding.etLoginEnter.text.toString()
                                && snap.child("password").getValue<String?>().toString() == binding.etPasswordEnter.text.toString()) {
                                    saveUserLogin(requireContext(), binding.etLoginEnter.text.toString())
                                    Toast.makeText(context, "Вы успешно авторизовались", Toast.LENGTH_SHORT).show()
                                    navController.navigate(R.id.action_mainFragment_to_catalogFragment)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Ошибка: $error", Toast.LENGTH_SHORT).show()
                    }

                })
            } else {
                Toast.makeText(requireContext(), "Введите все поля для авторизации", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    fun saveUserLogin(context: Context, username: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

}