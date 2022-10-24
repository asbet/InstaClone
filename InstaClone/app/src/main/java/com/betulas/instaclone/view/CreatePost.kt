package com.betulas.instaclone.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.betulas.instaclone.Model.Post
import com.betulas.instaclone.databinding.ActivityCreatePostAcitivityBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class CreatePost : AppCompatActivity() {

    private lateinit var binding:ActivityCreatePostAcitivityBinding
    private lateinit var activityReslutLauncher:ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    var selectedPictureForUri:Uri?=null
    private lateinit var fAuth:FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var storage:FirebaseStorage
    private lateinit var postArrayList:ArrayList<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostAcitivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        launchers()
        fAuth= Firebase.auth
        storage=Firebase.storage
        db=Firebase.firestore
        postArrayList =ArrayList<Post>()


    }

    fun imageViewselectImage(view: android.view.View){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED){
            //İzin yoksa iznin mantığını göstereceğiz. Ama bunu android bizim yerimize yapıcak.
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission Needed",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    //Request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else{
                //Request permission
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            val intentToGallery= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //Start activity for result
            activityReslutLauncher.launch(intentToGallery)

        }

    }

    fun launchers(){
        activityReslutLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result->
                if(result.resultCode== RESULT_OK){
                    val intentFromResult=result.data
                    if(intentFromResult !=null){
                        selectedPictureForUri=intentFromResult.data
                        selectedPictureForUri?.let {
                            binding.imageView3.setImageURI(it)

                        }

                    }
                }
            })
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if (result){
                //permission granted
                val intentToGallery= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityReslutLauncher.launch(intentToGallery)
            }else{
                //permission denied
                Toast.makeText(this@CreatePost,"Permission needed",Toast.LENGTH_LONG).show()
            }

        }

    }

    fun postButton(view: android.view.View){
        //Universal unique id
        val uuid=UUID.randomUUID()
        val imageRandomName="$uuid.jpg"
        val storage=Firebase.storage
        val reference=storage.reference

        val imageReferenceForUpload=reference.child("images").child(imageRandomName)
        if(selectedPictureForUri != null){
            imageReferenceForUpload.putFile(selectedPictureForUri !!).addOnSuccessListener {
            //Download url ->Firestore
                val uploadPictureReference=storage.reference.child("images").child(imageRandomName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl=it.toString()
                    val postMap= hashMapOf<String,Any>()
                    postMap.put("downloadUrl",downloadUrl)
                    postMap.put("userEmail",fAuth.currentUser!!.email.toString())
                    postMap.put("comment",binding.inputCommentText.text.toString())
                    postMap.put("date",Timestamp.now())
                    db.collection("Posts").add(postMap).addOnSuccessListener {task->
                         finish()
                    }.addOnFailureListener {
                        Toast.makeText(this@CreatePost,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }
                Toast.makeText(this,"Successful",Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }
}