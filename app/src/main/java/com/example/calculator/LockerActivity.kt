package com.example.calculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.calculator.databinding.ActivityLockerBinding
import com.example.calculator.documents.DocumentActivity
import com.example.calculator.notes.HomeActivity
import com.example.calculator.photos.PhotosActivity
import com.example.calculator.userAuthentication.LoginActivity
import com.example.calculator.userAuthentication.utlis.PrefManager
import com.example.calculator.videos.VideosActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LockerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLockerBinding
    private lateinit var prefManager: PrefManager
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager(this)


        binding.photos.setOnClickListener {
            val intent = Intent(this, PhotosActivity::class.java)
            startActivity(intent)
        }
        binding.videos.setOnClickListener {
            val intent = Intent(this, VideosActivity::class.java)
            startActivity(intent)
        }
        binding.notes.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        binding.document.setOnClickListener {
            val intent = Intent(this, DocumentActivity::class.java)
            startActivity(intent)
        }

        binding.logout.setOnClickListener {
            prefManager.clear()

            Firebase.auth.signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("786070384977-c314o3l7njd7nlgaufgifsvrrnv3s506.apps.googleusercontent.com")
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()


        }

    }
}