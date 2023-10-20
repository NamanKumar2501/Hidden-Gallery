package com.example.calculator.userAuthentication

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.calculator.databinding.ActivityResetPasswordBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        binding.resetButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()

            if (email.isNotEmpty()) {
                showProgressDialog(email)
            } else {
                binding.emailEditText.requestFocus()
                Snackbar.make(
                    binding.emailEditText,
                    "Password is Mandatory.",
                    Snackbar.LENGTH_SHORT
                ).show();
            }
        }

    }

    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "We have sent you a Email.", Toast.LENGTH_SHORT).show()

                    // Password reset email sent successfully
                    // You can show a success message to the user or navigate to another screen
                } else {
                    Toast.makeText(this, "Something went wrong !", Toast.LENGTH_SHORT).show()

                    // Password reset failed
                    // You can show an error message to the user
                    // showErrorDialog("Password Reset Failed", task.exception?.message ?: "Unknown error occurred")
                }
            }
    }

    private fun showProgressDialog(email: String) {

        if (this != null) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Update Password")
                .setMessage("Do you want to Update Password ?")
                .setNegativeButton("No", object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.dismiss()
                    }
                })
                .setPositiveButton("Yes",object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        resetPassword(email)
                        dialog?.dismiss()
                    }

                }).show()
        }
    }

}