package com.example.museum

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.museum.adapter.FeedRecyclerAdapter
import com.example.museum.databinding.ActivityUserAcitivtyBinding
import com.example.museum.model.Posts
import com.example.museum.view.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

private lateinit var binding: ActivityUserAcitivtyBinding
private lateinit var auth: FirebaseAuth
private lateinit var db:FirebaseFirestore
private  lateinit var postArryList:ArrayList<Posts>
private lateinit var feedRecyclerAdapter: FeedRecyclerAdapter

class UserAcitivty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserAcitivtyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = Firebase.auth
        db = Firebase.firestore
        //başlangıçta boş bir arrylist
        postArryList =ArrayList<Posts>()
        getdata()

        feedRecyclerAdapter= FeedRecyclerAdapter(postArryList)
        binding.reyclerview.layoutManager=LinearLayoutManager(this@UserAcitivty)
        binding.reyclerview.adapter= feedRecyclerAdapter
    }
    private fun getdata(){
        db.collection("Posts").addSnapshotListener { value, error ->
            if (error!=null){

                Toast.makeText(this@UserAcitivty, error.localizedMessage, Toast.LENGTH_LONG).show()
            }else{
                    if (value != null){
                        if (!value.isEmpty){
                            //dokümanları veriiri
                            val documents=value.documents
                            for (document in documents){
                                //casting
                                val id=document.id
                                val name=document.get("name") as String
                                val year=document.get("year") as String
                                val explanation=document.get("explanation") as String
                                val downloadUrl=document.get("downloadUrl") as String
                                val posts= Posts(name, year, downloadUrl, explanation,id)
                                postArryList.add(posts)

                            }
                            //veritanının değiştiğne haber ver
                        feedRecyclerAdapter.notifyDataSetChanged()
                        }
                    }
            }
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.museum_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId== R.id.logout){

            auth.signOut()
            val intenMain= Intent(this@UserAcitivty, MainActivity::class.java)
            startActivity(intenMain)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}