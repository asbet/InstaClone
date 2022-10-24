package com.betulas.instaclone.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.betulas.instaclone.Adapter.RecyclerAdapter
import com.betulas.instaclone.Model.Post
import com.betulas.instaclone.R
import com.betulas.instaclone.databinding.ActivityFeedBinding
import com.betulas.instaclone.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedActivity : AppCompatActivity() {
    val postList:ArrayList<Post> = ArrayList()
    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth:FirebaseAuth
    var recyclerAdapter :RecyclerAdapter ?=null
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth=FirebaseAuth.getInstance()
        db=FirebaseFirestore.getInstance()
        auth= Firebase.auth
        getData()

        binding.recyclerView.layoutManager= LinearLayoutManager(this)
        recyclerAdapter=RecyclerAdapter(postList)
        binding.recyclerView.adapter=recyclerAdapter
    }


    private fun getData(){
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if(error !=null){
                Toast.makeText(this,error.localizedMessage, Toast.LENGTH_LONG).show()
            }else{
                if(value!=null){
                    if (!value.isEmpty){
                        postList.clear()
                        val documents=value.documents
                        for (document in documents){
                            //casting
                            val comment=document.get("comment") as String
                            val userEmail=document.get("userEmail") as String
                            val downloadUrl=document.get("downloadUrl") as String
                            val post=Post(userEmail,comment,downloadUrl)
                            postList.add(post)
                        }
                        recyclerAdapter!!.notifyDataSetChanged()
                    }

                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.inst_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.addPost){

            val intent= Intent(this,CreatePost::class.java)
            startActivity(intent)
        }else if (item.itemId==R.id.signOutmenu){
            auth.signOut()
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()

        }
        return super.onOptionsItemSelected(item)
    }
}