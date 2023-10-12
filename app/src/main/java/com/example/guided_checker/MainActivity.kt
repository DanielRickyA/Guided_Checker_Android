package com.example.guided_checker

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.guided_checker.data.remote.service.ApiConfig
import com.example.guided_checker.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch(Dispatchers.Main) {
            val textOutputElement = binding.helloWorldElement // only the original thread that created a view hierarchy can touch its views
            try {
                Log.e("MainActivity", "Before Response")
                val response = ApiConfig.getApiService().getMahasiswa("A", "1")
                Log.e("MainActivity", response.toString())
                val data = response.data
                var textOutput = ""
                for (mahasiswa in data) {
                    textOutput += "${mahasiswa.nama} - ${if(mahasiswa.status?.status == "1") "Sudah" else "Belum"}\n"
                }
                textOutputElement.text = textOutput
            } catch (e: Exception) {
                textOutputElement.text = e.message
            }
        }
    }
}