package com.example.calculator.documents

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.R
import com.example.calculator.videos.VideoModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class DocumentAdapter(private val context: Context, private val documents: List<Document>) :
    RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val documentName: TextView = itemView.findViewById(R.id.documentName)
        val documentItem: LinearLayout = itemView.findViewById(R.id.document_item)
        val documentUrl: TextView = itemView.findViewById(R.id.documentUrl)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_document, parent, false)
        return DocumentViewHolder(view)
    }


    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = documents[position]
        holder.documentName.text = document.name
        holder.documentUrl.text = document.downloadUrl

        holder.itemView.setOnClickListener {
            openDocument(document.uri)
        }

        holder.documentItem.setOnLongClickListener(View.OnLongClickListener {
            showDialog(document)
            true
        })


    }

    private fun showDialog(document: Document) {

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val firebaseStorage =
            FirebaseStorage.getInstance().getReference(uid).child("documents/")

        MaterialAlertDialogBuilder(context)
            .setTitle("Delete Document")
            .setMessage("Do you want to delete this Document ?")
            .setNegativeButton("No", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                }
            })
            .setPositiveButton("Yes",object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    firebaseStorage.storage.getReferenceFromUrl(document.downloadUrl!!).delete().addOnSuccessListener(object :
                        OnSuccessListener<Void> {
                        override fun onSuccess(p0: Void?) {
                            Toast.makeText(context, "Document deleted", Toast.LENGTH_SHORT).show()
                            firebaseStorage.child(document.id).delete()

                            // Changes=================


                            val intent = Intent(context,DocumentActivity::class.java) // use to refresh this activity....

                            context.startActivity(intent)

                            // Optionally, you can finish the current activity if needed
                            (context as Activity).finish()
                            // ====================

                            notifyDataSetChanged()
                        }
                    })
                        .addOnFailureListener(object : OnFailureListener {
                            override fun onFailure(p0: Exception) {
                                notifyDataSetChanged()
                            }

                        })
                }

            }).show()

    }


    override fun getItemCount(): Int {
        return documents.size
    }

    private fun openDocument(documentUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(documentUri, "application/pdf") // Change the MIME type if opening different types of documents
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant read permission to the app
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No application available to open the document", Toast.LENGTH_SHORT).show()
        }
    }
}