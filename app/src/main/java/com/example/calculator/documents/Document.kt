package com.example.calculator.documents

import android.net.Uri

data class Document(
    val id: String, // Unique identifier for the document (e.g., document name or an ID from Firebase)
    val name: String, // Name of the document
    val uri: Uri, // URI of the local document on the device
    val downloadUrl: String // Download URL of the document from Firebase Storage
)