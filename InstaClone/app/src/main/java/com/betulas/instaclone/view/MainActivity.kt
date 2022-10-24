package com.betulas.instaclone.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.betulas.instaclone.R
import com.betulas.instaclone.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.core.View
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        firebaseAuth= Firebase.auth

        val currentUser=firebaseAuth.currentUser
        if(currentUser!=null){
            val intent=Intent(applicationContext,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    fun signUpClicked(view:android.view.View){
        try{
            val email=binding.inputMail.text.toString()
            val password=binding.passwr.text.toString()

            if(email.equals("")||password.equals("")){
                Toast.makeText(this@MainActivity,"Unsuccessful",Toast.LENGTH_LONG).show()
            }
            else{
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                    val intent=Intent(applicationContext,FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun signInClicked(view:android.view.View){
        val email=binding.inputMail.text.toString()
        val password=binding.passwr.text.toString()
        if (email.equals("")||password.equals("")){
            Toast.makeText(this,"You must sure fill in mail and password",Toast.LENGTH_LONG).show()

        }else{
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent=Intent(applicationContext,FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }
}