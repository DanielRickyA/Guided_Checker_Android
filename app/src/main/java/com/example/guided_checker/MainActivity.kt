package com.example.guided_checker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.guided_checker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener{
            val kelas = binding.listKelas.selectedItem.toString()
            val modul = binding.listModul.selectedItem.toString()

            val intent = Intent(this, KoreksiActivity::class.java)
            intent.putExtra("kelas", kelas)
            intent.putExtra("modul", modul)
            startActivity(intent)
        }


    }
}