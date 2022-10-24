package com.betulas.instaclone.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.betulas.instaclone.Model.Post
import com.betulas.instaclone.databinding.RecyclerRowBinding
import com.betulas.instaclone.view.CreatePost
import com.squareup.picasso.Picasso

class RecyclerAdapter(private val commentList:ArrayList<Post>): RecyclerView.Adapter<RecyclerAdapter.postHolder>() {
    class postHolder(val binding: RecyclerRowBinding): RecyclerView.ViewHolder(binding.root) {

    }
    //When we are creating postHolder,what is going
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): postHolder {
        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return postHolder(binding)
    }

    //When we are showing data and connecting data, what is going
    override fun onBindViewHolder(holder: postHolder, position: Int) {
        holder.binding.recyclerCommentText.text=commentList.get(position).comment
        holder.binding.recyclerEmailText.text=commentList.get(position).email
        Picasso.get().load(commentList[position].downloadUrl).into(holder.binding.recyclerImage)

    }

    //How much are we creating?
    override fun getItemCount(): Int {
        return commentList.size
    }
}