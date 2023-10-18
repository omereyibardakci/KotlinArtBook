package com.asus.kotlinartbook

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asus.kotlinartbook.databinding.RecyclerRowBinding

class ArtAdapter(val artArrayList: ArrayList<Art>): RecyclerView.Adapter<ArtAdapter.ArtHolder>() {

    class ArtHolder (val binding: RecyclerRowBinding):RecyclerView.ViewHolder(binding.root){
        // item view  yerine      binding değştiricez
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {

        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArtHolder(binding)

    }

    override fun getItemCount(): Int {
        return artArrayList.size
    }



    override fun onBindViewHolder(holder: ArtHolder, position: Int) {

        holder.binding.recyclerViewTextView.text = artArrayList.get(position).name

        // it will be shown when you click on recyclerView
        // recyclerView'da  tıklayınca kaydedilmiş verileri göstermek için
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ArtActivity::class.java)
            intent.putExtra("info", "old")
            intent.putExtra("id",artArrayList[position].id)
            holder.itemView.context.startActivity(intent)
        }


    }

}