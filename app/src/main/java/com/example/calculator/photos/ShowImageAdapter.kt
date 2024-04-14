package com.example.calculator.photos

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.calculator.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShowImageAdapter(
    private val imageList: ArrayList<ImageModel>,
    private val context: Context
): RecyclerView.Adapter<ShowImageAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val currentImage = imageList[position]

        holder.image.setOnClickListener {
            val intent = Intent(holder.itemView.context, ShowFullImageActivity::class.java)
            //listener?.onClick(AlbumsData)
            intent.putExtra("image", currentImage.url)
            holder.itemView.context.startActivity(intent)
        }

        holder.imageName.text = currentImage.name
/*
        Picasso
            .get()
            .load(currentImage.url)
            .into(holder.image)*/

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Execute image loading in IO dispatcher
                val bitmap = withContext(Dispatchers.IO) {
                    Glide.with(holder.itemView.context)
                        .asBitmap()
                        .load(currentImage.url)
                        .submit()
                        .get()
                }

                // Update ImageView with the loaded bitmap using main dispatcher
                holder.image.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
//                holder.image.setImageResource(R.drawable.placeholder_image)
            }
        }


        /* Glide.with(holder.itemView.context)
             .load(currentImage.url)
             .into(holder.image)
*/
         holder.listview_layout.setOnLongClickListener(OnLongClickListener {
             showDialog(currentImage)
             true
         })


       /* holder.itemView.setOnClickListener {
            // Handle item click
        }*/


    }



    override fun getItemCount(): Int {

            return imageList.size
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        val image :ImageView = view.findViewById(R.id.imageView)
        val imageName :TextView = view.findViewById(R.id.textView)
        val listview_layout :ConstraintLayout = view.findViewById(R.id.listview_layout)

    }

    //    private fun showDialog(imageModel: ImageModel) {
//        val uid = FirebaseAuth.getInstance().currentUser!!.uid
//
//        val firebaseStorage =
//            FirebaseStorage.getInstance().getReference(uid).child("images/")
//        val databaseRef =
//            FirebaseDatabase.getInstance().getReference(uid).child("images/")
//
//        MaterialAlertDialogBuilder(context)
//            .setTitle("Delete Image")
//            .setMessage("Do you want to delete this Image ?")
//            .setNegativeButton("No", object : DialogInterface.OnClickListener{
//                override fun onClick(dialog: DialogInterface?, which: Int) {
//                    dialog?.dismiss()
//                }
//            })
//            .setPositiveButton("Yes",object : DialogInterface.OnClickListener{
//                override fun onClick(dialog: DialogInterface?, which: Int) {
//                    firebaseStorage.storage.getReferenceFromUrl(imageModel.url!!).delete().addOnSuccessListener(object : OnSuccessListener<Void>{
//                        override fun onSuccess(p0: Void?) {
//                            Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show()
//
//                            databaseRef.child(imageModel.imageId.toString()).removeValue()
//                            imageList.remove(imageModel)
//
//                        }
//                    })
//                        .addOnFailureListener(object :OnFailureListener{
//                            override fun onFailure(p0: Exception) {
//
//                            }
//
//                        })
//                }
//
//            }).show()
//    }


    private fun showDialog(imageModel: ImageModel) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val firebaseStorage = FirebaseStorage.getInstance().getReference(uid).child("images/")
        val databaseRef = FirebaseDatabase.getInstance().getReference(uid).child("images/")

        MaterialAlertDialogBuilder(context)
            .setTitle("Delete Image")
            .setMessage("Do you want to delete this Image?")
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Yes") { _, _ ->
                val imageRef = firebaseStorage.storage.getReferenceFromUrl(imageModel.url!!)
                imageRef.delete()
                    .addOnSuccessListener {
                        // Image deleted successfully from storage, now delete from database
                        databaseRef.child(imageModel.imageId.toString()).removeValue()
                            .addOnSuccessListener {
                                // Database entry removed, now update the RecyclerView
                                imageList.remove(imageModel)
                                notifyItemRemoved(itemCount)
                                Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Failed to delete image from database",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("ShowImageAdapter", "Failed to delete image from database", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to delete image", Toast.LENGTH_SHORT).show()
                        Log.e("ShowImageAdapter", "Failed to delete image", e)
                    }
            }
            .show()
    }


}



