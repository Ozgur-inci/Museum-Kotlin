package com.example.museum.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.museum.R
import com.example.museum.databinding.ActivityAdminBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.UUID

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private  lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    var selectedpicture: Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        resultlauncher()
        auth=Firebase.auth
        firestore= Firebase.firestore
        storage=Firebase.storage

        val intent=intent
        val info=intent.getStringExtra("info")
        if (info.equals("new")){
            binding.uploadnametxt.setText("")
            binding.uploadyeartxt.setText("")
            binding.uploadexplanationtxt.setText("")
            binding.upload.visibility=View.VISIBLE
            binding.imageView.setImageResource(R.drawable.selimage)

        }else{

            val selectedid=intent.getStringExtra("id")
            if (selectedid!=null){
            fetchPostsdata(selectedid)
            }


        }



    }
    private fun fetchPostsdata(documentId: String){
        firestore.collection("Posts").document(documentId).get().addOnSuccessListener {document->

        if (document!=null && document.exists()){
            //casting
            val name=document.get("name") as String
            val year=document.get("year") as String
            val explanation=document.get("explanation") as String
            val downloadUrl=document.get("downloadUrl") as String
            UpdateUIWithPostdata(name, year, explanation, downloadUrl)

        }else{

            Toast.makeText(this@AdminActivity,"veritabanını kontrol edin",Toast.LENGTH_LONG).show()
        }

        }.addOnFailureListener(){

            Toast.makeText(this@AdminActivity,it.localizedMessage,Toast.LENGTH_LONG).show()

        }



    }

    private fun UpdateUIWithPostdata(name:String,year:String,explanation:String,downloadUrl:String){

        binding.uploadnametxt.setText(name)
        binding.uploadyeartxt.setText(year)
        binding.uploadexplanationtxt.setText(explanation)
        Picasso.get().load(downloadUrl).into(binding.imageView)
        binding.upload.visibility=View.INVISIBLE



    }


    fun selectimage(view: View){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
        if (ContextCompat.checkSelfPermission(this@AdminActivity,Manifest.permission.READ_MEDIA_IMAGES)!=PackageManager.PERMISSION_GRANTED){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this@AdminActivity,Manifest.permission.READ_MEDIA_IMAGES)){
            Snackbar.make(view,"permission need to gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permission"){
            //request  permission
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

            }.show()

        }else{
            //request permission
            permissionLauncher.launch((Manifest.permission.READ_MEDIA_IMAGES))

        }

        }else{
            val intenttogallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //startactivityforResult
            activityResultLauncher.launch(intenttogallery)

        }



        }else{
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"permission need for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permission"){
                        // request permission
                        permissionLauncher.launch((Manifest.permission.READ_EXTERNAL_STORAGE))
                    }.show()
                }else{
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

            }else{
                // go to gallery
                val intentgallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentgallery)
            }
        }
    }

    fun uploadbutton(view: View){

        val uuid=UUID.randomUUID()
        val imagename="$uuid.jpg"

       val reference = storage.reference
        val imagereferance=reference.child("images").child(imagename)
            if (selectedpicture!=null){
                //null olmadığından emin olunması isteniyor
                imagereferance.putFile(selectedpicture!!).addOnSuccessListener(){
                //download url-->firestore
                    val uploadpictureference=storage.reference.child("images").child(imagename)
                    uploadpictureference.downloadUrl.addOnSuccessListener {
                        val downloadurl=it.toString()

                        //any herhangi bir veritipi olduğnu gösterir

                        if(auth.currentUser!=null){
                            val postmap= hashMapOf<String,Any>()
                        postmap.put("downloadUrl",downloadurl)
                        postmap.put("name",binding.uploadnametxt.text.toString())
                        postmap.put("year",binding.uploadyeartxt.text.toString())
                        postmap.put("explanation",binding.uploadexplanationtxt.text.toString())

                            firestore.collection("Posts").add(postmap).addOnSuccessListener {
                                finish()

                            }.addOnFailureListener {
                                Toast.makeText(this@AdminActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                            }
                        }

                    }


                }.addOnFailureListener(){

                    Toast.makeText(this@AdminActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun resultlauncher(){
        //** her harikarda eklemelisin burası önemli  burda Gallery ekranındayken gerçekleştirilen olayları sunuyorsun **
        //gallery işlemi için activity başlatma ve resmin nerede olduğnu gösterme
        //Activity sonrası bir sonuç almak istiyorum almak istediğim sonuç ise Resimin url (Uri si)
activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

    if (result.resultCode== RESULT_OK){
        //uri alma
        val intentfromresult=result.data
        if (intentfromresult!=null){

            selectedpicture=intentfromresult.data
            selectedpicture?.let {
                binding.imageView.setImageURI(it)
            }
        }
    }
}

permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
    if (result){
        val intenttogallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(intenttogallery)

    }else{
        Toast.makeText(this@AdminActivity,"permission needed !",Toast.LENGTH_LONG).show()
    }
}
    }
}