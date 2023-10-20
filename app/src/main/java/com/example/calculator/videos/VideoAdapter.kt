package com.example.calculator.videos

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.calculator.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage


interface VideoItemClickListener {
    fun onVideoItemClicked(videoUrl: String)
}

class VideoAdapter(private val videoItems: MutableList<VideoModel>, private val clickListener: VideoItemClickListener, private val context: Context) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoItem = videoItems[position]
        holder.bind(videoItem)
    }

    override fun getItemCount(): Int {
        return videoItems.size
    }

    fun updateData(newVideoItems: List<VideoModel>) {
        videoItems.clear()
        videoItems.addAll(newVideoItems)
        notifyDataSetChanged()
    }


    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val videoThumbnailImageView: ImageView = itemView.findViewById(R.id.videoThumbnailImageView)
        private val videoNameTextView: TextView = itemView.findViewById(R.id.videoNameTextView)

        fun bind(videoItem: VideoModel) {
            // Load video thumbnail using Glide
            Glide.with(itemView.context)
                .load(videoItem.videoUrl)
                .into(videoThumbnailImageView)

            videoNameTextView.text = videoItem.videoName
            itemView.setOnClickListener {
                clickListener.onVideoItemClicked(videoItem.videoUrl)
            }

            itemView.setOnLongClickListener(View.OnLongClickListener {
                showDialog(videoItem)
                true
            })

        }
    }

    private fun showDialog(videoItem: VideoModel) {

        val firebaseStorage = FirebaseStorage.getInstance().getReference("videos/")
      //  val databaseRef = FirebaseDatabase.getInstance().getReference("images")

        MaterialAlertDialogBuilder(context)
            .setTitle("Delete Video")
            .setMessage("Do you want to delete this Video ?")
            .setNegativeButton("No", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                }
            })
            .setPositiveButton("Yes",object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    firebaseStorage.storage.getReferenceFromUrl(videoItem.videoUrl!!).delete().addOnSuccessListener(object :
                        OnSuccessListener<Void> {
                        override fun onSuccess(p0: Void?) {
                            Toast.makeText(context, "Video deleted", Toast.LENGTH_SHORT).show()

                            firebaseStorage.child(videoItem.videoUrl).delete()
                            videoItems.remove(videoItem)

                            notifyDataSetChanged()
                        }
                    })
                        .addOnFailureListener(object : OnFailureListener {
                            override fun onFailure(p0: Exception) {

                            }

                        })
                }

            }).show()
    }


}
