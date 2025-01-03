package com.example.lib.files

data class UploadFileResult(
    val success: Boolean = false,
    val message: String = "",
    val fileName: String? = null
)