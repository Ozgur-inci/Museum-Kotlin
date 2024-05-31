package com.example.museum.view

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.museum.UserAcitivty
import com.example.museum.databinding.ActivityMainBinding
import com.google.firebase.auth.auth


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth=Firebase.auth
        val currentuser=auth.currentUser
        if (currentuser!=null){
            val intent=Intent(this@MainActivity, UserAcitivty::class.java)
            startActivity(intent)
            finish()
        }



    }
fun SignupButton(view:View){

    val Signupintent=Intent(this@MainActivity, SignupAcitivity::class.java)
    startActivity(Signupintent)
}
    fun Loginbutton(view: View){
    val logemail=binding.loginemail.text.toString()
        val logpasswd=binding.loginPassword.text.toString()
        if (logemail.equals("")||logpasswd.equals("")){
            Toast.makeText(this@MainActivity,"lütfen boş alan bırakmayınız",Toast.LENGTH_LONG).show()
        }
        else if (logemail.equals("admin@gmail.com") && logpasswd.equals("admin1")){
            auth.signInWithEmailAndPassword(logemail,logpasswd).addOnSuccessListener {
                val adminintet=Intent(this@MainActivity, AdminActivity::class.java)
                adminintet.putExtra("info","new")
                startActivity(adminintet)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }

        }
        else{
                auth.signInWithEmailAndPassword(logemail,logpasswd).addOnSuccessListener {


                    val logintent=Intent(this@MainActivity, UserAcitivty::class.java)
                    startActivity(logintent)
                    finish()

                }.addOnFailureListener {

                    Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                }


        }


    }
}