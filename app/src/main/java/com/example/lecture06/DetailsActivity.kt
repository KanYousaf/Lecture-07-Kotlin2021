package com.example.lecture06

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
    }

    fun saveButtonClicked(view: View) {
        Glide.with(this)
            .asBitmap()
            .load("https://source.unsplash.com/random/600Ã—500/?cat")
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .signature(ObjectKey(System.currentTimeMillis().toString()))
            .into(object : CustomTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL){
                override fun onLoadCleared(placeholder: Drawable?) {
                    TODO("Not yet implemented")
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    saveImage(resource)
                }

            })
    }

    //code to save the image
    private fun saveImage(resource: Bitmap) {
        val contentValues = contentValues()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Lecture06/")
            contentValues.put(MediaStore.Images.Media.IS_PENDING, true)
        }
        val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            saveImageToStream(resource, contentResolver.openOutputStream(uri))
            contentValues.put(MediaStore.Images.Media.IS_PENDING, false)
            contentResolver.update(uri, contentValues, null, null)
        } else {
            val storageDir = File(Environment.getExternalStorageDirectory().absolutePath + "/Lecture06/")
            // getExternalStorageDirectory is deprecated in API 29

            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            val Random_Number = (0..1000000).random()
            val imageFileName = "JPEG_" + Random_Number + ".jpg"
            val imageFile = File(storageDir, imageFileName)
            saveImageToStream(resource, FileOutputStream(imageFile))
            if (imageFile.absolutePath != null) {
                contentValues.put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                // .DATA is deprecated in API 29
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            }
        }
        Toast.makeText(this, "IMAGE SAVED", Toast.LENGTH_LONG).show()
    }

    private fun contentValues(): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
