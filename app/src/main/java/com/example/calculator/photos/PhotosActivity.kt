package com.example.calculator.photos

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.LockerActivity
import com.example.calculator.R
import com.example.calculator.databinding.ActivityPhotosBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

class PhotosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotosBinding
    private lateinit var uri:Uri
    private lateinit var imageList: ArrayList<ImageModel>
    private var isGridView = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // binding.iconView.text = "List View"

        setSupportActionBar(binding.toolbar)

        binding.addImage.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 71)

        }

        binding.toolbar.setNavigationOnClickListener(View.OnClickListener {
            val main = Intent(applicationContext, LockerActivity::class.java)
            startActivity(main)
        }) 

        imageList = arrayListOf()

        val uid = FirebaseAuth.getInstance().currentUser!!.uid


        val databaseReference =  FirebaseDatabase.getInstance().getReference(uid).child("images/")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                imageList.clear()
                Log.i(ContentValues.TAG, "User Image $snapshot")
                for (dataSnapshot in snapshot.children) {

                    val image: ImageModel? = dataSnapshot.getValue(ImageModel::class.java)
                    if (image != null) {
                        imageList.add(image)
                    }

                }
                  binding.recyclerview.layoutManager = LinearLayoutManager(this@PhotosActivity)
                binding.recyclerview.adapter = ShowImageAdapter(imageList,this@PhotosActivity)



                binding.recyclerview.layoutManager = GridLayoutManager(this@PhotosActivity, 4)
                binding.recyclerview.adapter = ShowGridViewImageAdapter(imageList,this@PhotosActivity)



            }



            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PhotosActivity,error.toString(),Toast.LENGTH_SHORT).show()
            }


        })





    }

    private fun uploadFile(uri: Uri) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        if (uri != null) {
            val originalFileName = getFileName(uri)
            val firebaseStorage =
                FirebaseStorage.getInstance().getReference(uid).child("images/$originalFileName")
            val databaseRef =
                FirebaseDatabase.getInstance().getReference(uid).child("images/")

            val storageRef = firebaseStorage.child(
                System.currentTimeMillis().toString() + "." + getFileExtension(this.uri)
            )

            val processDialog = ProgressDialog(this@PhotosActivity)
            processDialog.setMessage("Photo Uploading")
            processDialog.setCancelable(false)
            processDialog.show()

            storageRef.putFile(this.uri)
                .addOnSuccessListener {

                    Log.i(ContentValues.TAG, "onSuccess Main: $it")
                    processDialog.dismiss()
                    Toast.makeText(
                        this@PhotosActivity,
                        "Upload Image Successfully",
                        Toast.LENGTH_SHORT
                    ).show()


                    val urlTask: Task<Uri> = it.storage.downloadUrl
                    while (!urlTask.isSuccessful);
                    val downloadUrl: Uri = urlTask.result
                    Log.i(ContentValues.TAG, "onSuccess: $downloadUrl")

                    val imageModel =
                        ImageModel(databaseRef.push().key, originalFileName, downloadUrl.toString())
                    val uploadId = imageModel.imageId

                    if (uploadId != null) {
                        databaseRef.child(uploadId).setValue(imageModel)
                    }


        }

            .addOnFailureListener {

                Toast.makeText(this@PhotosActivity, "Failed to Upload Image", Toast.LENGTH_SHORT)
                    .show()
                processDialog.dismiss()

            }
                .addOnProgressListener { taskSnapshot -> //displaying the upload progress
                    val progress =
                        100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    processDialog.setMessage("Uploaded " + progress.toInt() + "%...")
                }
    }

    }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        var result = ""
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        return result
    }


    private fun getFileExtension(uri: Uri): String? {
        val cR: ContentResolver = this.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 71 && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            uri = data.data!!

            uploadFile(uri)
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.photos_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.view -> {

                if (isGridView) {
                    binding.recyclerview.layoutManager = LinearLayoutManager(this,
                        RecyclerView.VERTICAL,false)
                    //   binding.iconView.setImageResource(R.drawable.ic_grid_view)
                    binding.recyclerview.adapter = ShowImageAdapter(imageList,this@PhotosActivity)
                   // changeMenuItemText("Grid View")
                    item.setTitle("Grid View")

                } else {
                    binding.recyclerview.layoutManager = GridLayoutManager(this, 4)
                    //  binding.iconView.setImageResource(R.drawable.ic_list_view)
                    binding.recyclerview.adapter = ShowGridViewImageAdapter(imageList,this@PhotosActivity)
                    item.setTitle("List View")
                }
                isGridView = !isGridView

                true
            }
           /* R.id.delete_all ->{
                Toast.makeText(this@PhotosActivity,"Delete all clicked", Toast.LENGTH_SHORT).show();
                return true
            }*/

            else -> super.onOptionsItemSelected(item)
        }
    }


}