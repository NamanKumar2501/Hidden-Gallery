package com.example.calculator.documents



import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.R

class DocumentAdapter(private val context: Context, private val documents: List<Document>) :
    RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val documentName: TextView = itemView.findViewById(R.id.documentName)
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
