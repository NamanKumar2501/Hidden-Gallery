package com.example.calculator.userAuthentication

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.calculator.databinding.ActivityRegistrationBinding
import com.example.calculator.userAuthentication.utlis.PrefManager

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var prefManager: PrefManager
    private lateinit var auth: FirebaseAuth
    private var userModal: UserModal? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        auth = Firebase.auth

        binding.signIn.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        prefManager = PrefManager(this)



        binding.signUpBtn.setOnClickListener {

            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (binding.nameEditText.text!!.isEmpty()) {
                binding.nameEditText.requestFocus()
                Snackbar.make(binding.nameEditText, "Name is Mandatory.", Snackbar.LENGTH_SHORT).show();

            } else if (binding.emailEditText.text!!.isEmpty()) {
                binding.emailEditText.requestFocus()
                Snackbar.make(binding.emailEditText, "Email is Mandatory.", Snackbar.LENGTH_SHORT).show();
            }
            else if (binding.passwordEditText.text!!.isEmpty()) {
                binding.passwordEditText.requestFocus()
                Snackbar.make(binding.passwordEditText,"Password is Mandatory.", Snackbar.LENGTH_SHORT).show();
            }else if (binding.confirmPasswordEditText.text!!.isEmpty()) {
                binding.confirmPasswordEditText.requestFocus()
                Snackbar.make(binding.confirmPasswordEditText, "Confirm Password is Mandatory.", Snackbar.LENGTH_SHORT).show();
            }

            else{


                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener

                        // else if successful
                        Log.d(TAG, "Successfully created user with uid: ${it.result!!.user?.uid}")
                        addDataToFirebase(name, email,password)
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Failed to create user: ${it.message}")
                        //  binding.loadingView.visibility = View.GONE
                        Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
                    }

                val database = FirebaseDatabase.getInstance()
                val userNameFatch: DatabaseReference = database.getReference("users") // Replace with your actual path

                userNameFatch.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for(postSnapshot in dataSnapshot.children){

                            userModal = postSnapshot.getValue(UserModal::class.java)
                            Log.i("homeUserData", "onCreateView: UserData > " + postSnapshot.value)



                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })



            }


        }

    }


    private fun addDataToFirebase(name: String, email: String, password: String) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val firebaseDatabase =  FirebaseDatabase.getInstance().getReference("users").child(uid)


        firebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                Log.i(ContentValues.TAG, "onSuccess Main: $snapshot")



                val userModal = UserModal(
                    uid,name, email, password
                )

                firebaseDatabase.setValue(userModal)
                if (snapshot.exists()) {
                    Snackbar.make(
                        binding.signUpBtn,
                        "Registration Successfully",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()

                } else {
                    Snackbar.make(binding.signUpBtn, "Something went Wrong", Snackbar.LENGTH_SHORT).show()
                }



            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RegistrationActivity,"Registration Failed.", Toast.LENGTH_SHORT).show();
            }

        })

    }

}