package com.example.calculator.photos


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
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

class ShowGridViewImageAdapter(
    private val imageList: ArrayList<ImageModel>,
    private val context: Context
): RecyclerView.Adapter<ShowGridViewImageAdapter.ViewHolder>() {

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        val image :ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.grid_view_list_item,parent,false)

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

       /* Picasso
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


        /*
                    Glide
                        .with(Context)
                    .load(imageModel.url)
                    .into(holder.image)*/


        holder.image.setOnLongClickListener(OnLongClickListener {
            showDialog(currentImage)
            true
        })


    }

    private fun showDialog(imageModel: ImageModel) {

        val uid = FirebaseAuth.getInstance().currentUser!!.uid


        val firebaseStorage =
            FirebaseStorage.getInstance().getReference(uid).child("images/")
        val databaseRef =
            FirebaseDatabase.getInstance().getReference(uid).child("images/")

        MaterialAlertDialogBuilder(context)
            .setTitle("Delete Image")
            .setMessage("Do you want to delete this Image ?")
            .setNegativeButton("No", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                }
            })
            .setPositiveButton("Yes",object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    firebaseStorage.storage.getReferenceFromUrl(imageModel.url!!).delete().addOnSuccessListener(object : OnSuccessListener<Void>{
                        override fun onSuccess(p0: Void?) {
                            Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show()

                            databaseRef.child(imageModel.imageId.toString()).removeValue()
                            imageList.remove(imageModel)
                            notifyDataSetChanged();

                        }
                    })
                        .addOnFailureListener(object :OnFailureListener{
                            override fun onFailure(p0: Exception) {

                            }

                        })
                }

            }).show()
    }



    override fun getItemCount(): Int {

        return imageList.size
    }
}
