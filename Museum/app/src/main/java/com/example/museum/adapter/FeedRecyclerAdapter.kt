package com.example.museum.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.museum.databinding.RecyclerRowBinding
import com.example.museum.model.Posts
import com.example.museum.view.AdminActivity

class FeedRecyclerAdapter(private  val PostList:ArrayList<Posts>):RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>(){

    class PostHolder(val binding: RecyclerRowBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context))
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return PostList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.reyclerviewtxtview.text=PostList.get(position).name
        holder.itemView.setOnClickListener {
            val intent=Intent(holder.itemView.context,AdminActivity::class.java)
            intent.putExtra("info","old")
            intent.putExtra("id",PostList.get(position).id)
            holder.itemView.context.startActivity(intent)
        }
    }
}