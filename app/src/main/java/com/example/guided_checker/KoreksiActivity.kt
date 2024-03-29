package com.example.guided_checker

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
    private var searchQuery = ""
    private var listMahasiswa: List<MahasiswaWithStatus> = listOf()
    private var showNullStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKoreksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kelas = intent.getStringExtra("kelas").toString()
        modul = intent.getStringExtra("modul").toString()
        supportActionBar?.hide()
        initRecycleView()
        getDataMahasiswa()

        binding.tvKelasModul.text = "Kelas $kelas - Modul $modul"

        binding.refreshLayout.setOnRefreshListener {
            getDataMahasiswa()
        }

        binding.search.editText?.addTextChangedListener {
            searchQuery = it.toString().lowercase()
            updateListMahasiswa()
        }

        binding.swFilter.setOnCheckedChangeListener { _, isChecked ->
            showNullStatus = isChecked
            updateListMahasiswa()
        }
    }

    private fun getDataMahasiswa(){
        binding.refreshLayout.isRefreshing = true
        CoroutineScope(Dispatchers.IO).launch(Dispatchers.Main) {
//            val textOutputElement = binding.helloWorldElement // only the original thread that created a view hierarchy can touch its views
            try {
                Log.e("MainActivity", "Before Response")
                val response = ApiConfig.getApiService().getMahasiswa(kelas, modul)
                Log.e("MainActivity", response.toString())
                listMahasiswa = response.data
                updateListMahasiswa()
                binding.refreshLayout.isRefreshing = false
            } catch (e: Exception) {
                binding.refreshLayout.isRefreshing = false
            }
        }
    }

    private fun updateListMahasiswa(){
        var list = listMahasiswa.filter {
            it.npm.contains(searchQuery) || it.nama.lowercase().contains(searchQuery)
        }
        if (showNullStatus) {
            list = list.filter {
                it.status == null
            }
        }
        adapter.setData(list)
    }

    private fun initRecycleView(){
        adapter = MahasiswaAdapter(object :MahasiswaAdapter.ClickInterface{
            override fun onClickItem(data: MahasiswaWithStatus){
                if (data.status != null) {
                    val id = data.status.id
                    AlertDialog.Builder(this@KoreksiActivity)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menghapus status presensi ${data.npm}?")
                        .setPositiveButton("Ya") { _, _ ->
                            CoroutineScope(Dispatchers.IO).launch(Dispatchers.Main) {
                                // set loading = true agar user tau kalau sedang loading
                                binding.refreshLayout.isRefreshing = true
                                try {
                                    ApiConfig.getApiService().deleteStatus(id)
                                    // reset search query
                                    searchQuery = ""
                                    binding.search.editText?.setText("")
                                    Toast.makeText(this@KoreksiActivity, "${data.npm} berhasil dihapus.", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(this@KoreksiActivity, "Gagal menghapus ${data.npm}.", Toast.LENGTH_SHORT).show()
                                } finally {
                                    // refresh data entah berhasil atau tidak
                                    // loading di-hide di getDataMahasiswa()
                                    getDataMahasiswa()
                                }
                            }
                        }
                        .setNegativeButton("Tidak", null)
                        .show()
                    return
                }
                CoroutineScope(Dispatchers.IO).launch(Dispatchers.Main) {
                    // set loading = true agar user tau kalau sedang loading
                    binding.refreshLayout.isRefreshing = true
                    try {
                        ApiConfig.getApiService().addStatus(data.npm, modul, "1")
                        // reset search query
                        searchQuery = ""
                        binding.search.editText?.setText("")
                        Toast.makeText(this@KoreksiActivity, "${data.npm} berhasil dikoreksi.", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@KoreksiActivity, "Gagal mengoreksi ${data.npm}.", Toast.LENGTH_SHORT).show()
                    } finally {
                        // refresh data entah berhasil atau tidak
                        // loading di-hide di getDataMahasiswa()
                        getDataMahasiswa()
                    }
                }
            }
        })
        binding.listMahasiswa.layoutManager = LinearLayoutManager(this)
        binding.listMahasiswa.adapter = adapter
    }
}

