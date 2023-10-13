package com.example.guided_checker


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.guided_checker.data.remote.model.MahasiswaWithStatus
import com.example.guided_checker.databinding.ItemMahasiswaBinding

class MahasiswaAdapter (
    private val callback: ClickInterface
): RecyclerView.Adapter<MahasiswaAdapter.ViewHolder>(){

    private val item: List<MahasiswaWithStatus> = ArrayList()

    fun setData(data: List<MahasiswaWithStatus>){
        (item as ArrayList).clear()
        item.addAll(data)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemMahasiswaBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: MahasiswaWithStatus){
            with(binding){
                npm.text = data.npm
                nama.text= data.nama

                btnCheck.setOnClickListener{
                    callback.onClickItem(data)
                }

                if (data.status != null) {
                    btnCheck.setBackgroundColor(Color.parseColor("#4CAF50"))
                } else {
                    btnCheck.setBackgroundColor(Color.parseColor("#ffffff"))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =ItemMahasiswaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = item[position]

        holder.bind(data)
    }

    interface ClickInterface{
        fun onClickItem(data: MahasiswaWithStatus)
    }
}