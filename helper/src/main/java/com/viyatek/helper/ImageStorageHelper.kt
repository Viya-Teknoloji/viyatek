package com.viyatek.helper

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import java.io.*


class ImageStorageHelper(val context : Context) {

    fun saveImage(image: Bitmap, folderName:String, fileName : String): Uri? {
        //TODO - Should be processed in another thread
        val imagesFolder = File(context.filesDir, folderName)
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "${fileName}.jpg")
            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.JPEG, 95, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: IOException) {
            Log.d("TAG", "IOException while trying to write file for sharing: " + e.message)
        }
        return uri
    }

    fun saveImage(image: Bitmap, fileName : String): Uri? {
        //TODO - Should be processed in another thread

        var uri: Uri? = null
        try {
            val file = File(context.filesDir, "${fileName}.jpg")
            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.JPEG, 95, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: IOException) {
            Log.d("TAG", "IOException while trying to write file for sharing: " + e.message)
        }
        return uri
    }

    fun galleryAddPic(theUri : Uri) {
        val file = theUri.toFile()
        MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null, null)
    }

    fun saveBitmapAsImageToDevice(bitmap: Bitmap?) : Uri? {
        // Add a specific media item.
        val resolver = context.contentResolver

        val imageStorageAddress = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "facie_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis())
        }

        try {
            // Save the image.
            val contentUri: Uri? = resolver.insert(imageStorageAddress, imageDetails)

            contentUri?.let { uri ->
                // Don't leave an orphan entry in the MediaStore
                if (bitmap == null) resolver.delete(contentUri, null, null)
                val outputStream: OutputStream? = resolver.openOutputStream(uri)
                outputStream?.let { outStream ->
                    val isBitmapCompressed = bitmap?.compress(Bitmap.CompressFormat.JPEG, 95, outStream)
                    if (isBitmapCompressed == true) {
                        outStream.flush()
                        outStream.close()

                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()

                        return uri
                    }
                } ?: throw IOException("Failed to get output stream.")
            } ?: throw IOException("Failed to create new MediaStore record.")
        } catch (e: IOException) {
            throw e
        }

         return null
    }

    fun saveMediaToStorage(bitmap: Bitmap) {
        //Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            context.contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        }
        else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)

            it.close()

            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveVideo(filePath: String?) {

        //Generating a file name
        val filename = "facie_${System.currentTimeMillis()}.mp4"

        //Output stream
        var fos: OutputStream? = null



            filePath?.let {

                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "video/mp4")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
                }


                val fileUri = context.contentResolver.insert(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    values
                )

                //Opening an outputstream with the Uri that we got
                fos = fileUri?.let { context.contentResolver.openOutputStream(it) }

            }

        fos?.use {outputStream ->

                        val videoFile = File(filePath)
                        FileInputStream(videoFile).use { inputStream ->
                            val buf = ByteArray(8192)
                            while (true) {
                                val sz = inputStream.read(buf)
                                if (sz <= 0) break
                                outputStream.write(buf, 0, sz)
                            }
                        }


            outputStream.close()

            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
        }

        }

    fun addVideo(videoFile: File, fileName:String): Uri? {
        val values = ContentValues(3)
        values.put(MediaStore.Video.Media.TITLE, fileName)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Video.Media.DATA, videoFile.absolutePath)
        return context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
    }

    fun getImageFromInternalStorage(context: Context, fileName : String): Bitmap? {
        val directory = context.filesDir
        val file = File(directory, fileName)

        return if(file.exists()) BitmapFactory.decodeStream(FileInputStream(file)) else null
    }

    fun getImageFromInternalStorage(context: Context, folderName:String, fileName : String): Bitmap? {
        val directory = "${context.filesDir}/$folderName"
        val file = File(directory, fileName)
        return if(file.exists()) BitmapFactory.decodeStream(FileInputStream(file)) else null
    }

    fun deleteImageFromInternalStorage(context: Context,  folderName:String, fileName : String): Boolean {
        val dir = "${context.filesDir}/$folderName"
        val file = File(dir, fileName)
        return file.delete()
    }

    fun deleteImageFromInternalStorage(context: Context, fileName: String): Boolean {
        val dir = context.filesDir
        val file = File(dir, fileName)
        return file.delete()
    }
}