package com.example.lib.files

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.net.URL
import java.util.UUID

class MinIoService(
    storagePath: String,
    storageUsername: String,
    storagePassword: String,
    private var storageBucket: String,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private var minioClient: MinioClient? = null

    init {
        try {
            minioClient = MinioClient.builder()
                .endpoint(storagePath)
                .credentials(storageUsername, storagePassword)
                .build()
            val hasBucket = minioClient?.bucketExists(BucketExistsArgs.builder().bucket(storageBucket).build())
            if (hasBucket == false) {
                logger.info("initStorage(): Creating bucket $storageBucket")
                minioClient?.makeBucket(MakeBucketArgs.Builder().bucket(storageBucket).build())
            }
            logger.info("initStorage(): Storage ${storageBucket} initialized")
        } catch (e: Exception) {
            logger.error("initStorage(): Error = ${e.localizedMessage}")
            e.printStackTrace()
        }
    }

    /**
     * @return name of saved file
     */
    fun saveFileToBucketFromUrl(url: String, hardExt: String? = null): UploadFileResult {
        val extension = hardExt?.replace(".", "") ?: url.substringAfterLast(".")
        val fileId = UUID.randomUUID().toString() + "_${kotlin.random.Random.nextInt(100)}"
        // Download file
        val bytes = try {
            URL(url).readBytes()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } ?: return UploadFileResult(false, message = "Failed to download file")
        // Save locally and upload to bucket
        val uploaded = saveFileAndUpload(bytes, fileId, extension)
        if (!uploaded) {
            return UploadFileResult(false, message = "Failed to upload file")
        }
        return UploadFileResult(true, fileName = "$fileId.$extension")
    }

    private fun saveFileAndUpload(byteArray: ByteArray, fileId: String, extension: String): Boolean {
        return uploadFileToBucket(byteArray, "$fileId.$extension")
    }

    /**
     * Because of using temp file, we need to set name not from file
     */
    fun uploadFileToBucket(byteArray: ByteArray, fileName: String): Boolean {
        val result = ByteArrayInputStream(byteArray).use { inputStream ->
            minioClient?.putObject(
                io.minio.PutObjectArgs.builder()
                    .bucket(storageBucket)
                    .`object`(fileName)
                    .stream(inputStream, byteArray.size.toLong(), 10485760)
                    .contentType("application/octet-stream")
                    .build()
            )
        }
        return result != null
    }

    fun getFileAsBytes(fileName: String): ByteArray? {
        return minioClient?.getObject(
            io.minio.GetObjectArgs.builder()
                .bucket(storageBucket)
                .`object`(fileName)
                .build()
        )?.readAllBytes()
    }

    fun deleteFile(fileName: String) {
        minioClient?.removeObject(
            io.minio.RemoveObjectArgs.builder()
                .bucket(storageBucket)
                .`object`(fileName)
                .build()
        )
    }

}