package com.example.guided_checker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.guided_checker.data.remote.model.MahasiswaWithStatus
import com.example.guided_checker.data.remote.service.ApiConfig
import com.example.guided_checker.databinding.ActivityKoreksiBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KoreksiActivity : AppCompatActivity() {
    private lateinit var kelas: String
    private lateinit var modul: String
    private lateinit var binding: ActivityKoreksiBinding
    private lateinit var adapter: MahasiswaAdapter
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKoreksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kelas = intent.getStringExtra("kelas").toString()
        modul = intent.getStringExtra("modul").toString()
        supportActionBar?.title = "Koreksi $kelas $modul"
        initRecycleView()
        getDataMahasiswa()

        binding.refreshLayout.setOnRefreshListener {
            getDataMahasiswa()
        }

        binding.search.editText?.addTextChangedListener {
            searchQuery = it.toString().lowercase()
            getDataMahasiswa()
        }
    }

    fun getDataMahasiswa(){
        binding.refreshLayout.isRefreshing = true
        CoroutineScope(Dispatchers.IO).launch(Dispatchers.Main) {
//            val textOutputElement = binding.helloWorldElement // only the original thread that created a view hierarchy can touch its views
            try {
                Log.e("MainActivity", "Before Response")
                val response = ApiConfig.getApiService().getMahasiswa(kelas, modul)
                Log.e("MainActivity", response.toString())
                val data = response.data.filter {
                    it.npm.contains(searchQuery) || it.nama.lowercase().contains(searchQuery)
                }
                adapter.setData(data)
                binding.refreshLayout.isRefreshing = false
            } catch (e: Exception) {
                binding.refreshLayout.isRefreshing = false
            }
        }
    }

    fun initRecycleView(){
        adapter = MahasiswaAdapter(object :MahasiswaAdapter.ClickInterface{
            override fun onClickItem(data: MahasiswaWithStatus){
                if (data.status != null) {
                    Toast.makeText(this@KoreksiActivity, "Sudah dikoreksi", Toast.LENGTH_SHORT).show()
                    return
                }
                CoroutineScope(Dispatchers.IO).launch(Dispatchers.Main) {
                    try {
                        ApiConfig.getApiService().addStatus(data.npm, modul, "1")
                        // reset search query
                        searchQuery = ""
                        binding.search.editText?.setText("")

                        getDataMahasiswa()
                    } catch (e: Exception) {
//                textOutputElement.text = e.message
                    }
                }
            }
        })
        binding.listMahasiswa.layoutManager = LinearLayoutManager(this)
        binding.listMahasiswa.adapter = adapter
    }
}

