package com.example.museum.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.museum.databinding.ActivitySignupAcitivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupAcitivity : AppCompatActivity() {
    private  lateinit var binding: ActivitySignupAcitivityBinding
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivitySignupAcitivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth=Firebase.auth


        
    }
    fun Save(view: View){
        val email=binding.emailSingup.text.toString()
        val passwd=binding.SinginPasswd.text.toString()
        val username=binding.SinginUsername.text.toString()
        val passwdagain=binding.SinginPasswdagain.text.toString()

        if(email.equals("") || passwd.equals("")||passwdagain.equals("")){
            Toast.makeText(this@SignupAcitivity,"Lütfen boş bırakmayınız",Toast.LENGTH_LONG).show()

        }
        else if (passwd.equals(passwdagain)){

            Toast.makeText(this@SignupAcitivity,"Şifre tekrarınız hatalıdır",Toast.LENGTH_LONG).show()

        }
        else{
            auth.createUserWithEmailAndPassword(email,passwd).addOnSuccessListener {
                val intent=Intent(this@SignupAcitivity, MainActivity::class.java)
                startActivity(intent)
                finish()

            }.addOnFailureListener {

                Toast.makeText(this@SignupAcitivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }
}