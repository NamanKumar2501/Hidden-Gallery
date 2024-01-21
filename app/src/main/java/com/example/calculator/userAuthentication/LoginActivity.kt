package com.example.calculator.userAuthentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.calculator.MainActivity
import com.example.calculator.databinding.ActivityLoginBinding
import com.example.calculator.userAuthentication.utlis.Constant
import com.example.calculator.userAuthentication.utlis.PrefManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefManager: PrefManager
    private lateinit var auth: FirebaseAuth
  //  private lateinit var firebaseDatabase: FirebaseDatabase
    // [END declare_auth]
    private lateinit var googleSignInClient: GoogleSignInClient
    private var userModal: UserModal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val firebaseDatabase = FirebaseDatabase.getInstance().getReference("user")

        binding.forgotPassword.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        // customizeGooglePlusButton(googleSignInButton)

        binding.googleSignIn.setOnClickListener {
            signIn()
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("786070384977-c314o3l7njd7nlgaufgifsvrrnv3s506.apps.googleusercontent.com")
         //   .requestIdToken("608878159622-kk79j9sumkocbg90ofe06fv9jtasajvd.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END config_signin]

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]

        val database = FirebaseDatabase.getInstance()
        val userNameFatch: DatabaseReference = database.getReference("users") // Replace with your actual path

        userNameFatch.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(postSnapshot in dataSnapshot.children){

                    userModal = postSnapshot.getValue(UserModal::class.java)
                    // Log.i("homeUserData", "onCreateView: UserData > " + postSnapshot.value)

                    userModal?.name?.let { prefManager.name(Constant.PREF_IS_NAME, it) }
                    userModal?.email?.let { prefManager.email(Constant.PREF_IS_EMAIL, it) }
                    userModal?.password?.let {prefManager.password(Constant.PREF_IS_PASSWORD,it)}


                    Log.i("homeUserData", userModal?.name+userModal?.email+userModal?.password)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        prefManager = PrefManager(this  )

        binding.signUp.setOnClickListener {

            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            finish()

        }


        binding.loginBtn.setOnClickListener {

            if (binding.emailEditText.text!!.isEmpty()) {
                binding.emailEditText.requestFocus()
                Snackbar.make(
                    binding.emailEditText,
                    "Password is Mandatory.",
                    Snackbar.LENGTH_SHORT
                ).show();
            } else if (binding.passwordEditText.text!!.isEmpty() ) {
                binding.passwordEditText.requestFocus()
                Snackbar.make(
                    binding.passwordEditText,
                    "Password is Mandatory.",
                    Snackbar.LENGTH_SHORT
                ).show();
            } else {

                val email = binding.emailEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString().trim()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener

                      //  prefManager.checkLogin(Constant.PREF_IS_LOGIN, true)
                        Log.d(TAG, "Successfully logged in: ${it.result!!.user?.uid}")
                        Toast.makeText(this, "${it.result}", Toast.LENGTH_SHORT).show()

                        prefManager.checkLogin(Constant.PREF_IS_LOGIN, true)

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                    }


            }

        }

    }
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)



        if(prefManager.getBoolean(Constant.PREF_IS_LOGIN)){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // [START signin]
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    // [END signin]

    // [START onactivityresult]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                updateUI(null)
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }
    private fun updateUI(user: FirebaseUser?) {
        if(user!=null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    // [END auth_with_google]

    /*fun customizeGooglePlusButton(signInButton: SignInButton) {
        for (i in 0..signInButton.childCount) {
            val v = signInButton.getChildAt(i)

            if (v is TextView) {
                val tv = v
                tv.setText("My Text")
                tv.setAllCaps(true)
                tv.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_launcher_foreground,
                    0,
                    0,
                    0
                )
                //here you can customize what you want
                return
            }
        }
    }*/

    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 1901
    }


}