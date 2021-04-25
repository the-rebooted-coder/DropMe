package com.onesilicondiode.dropme

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.theapache64.removebg.RemoveBg
import com.theapache64.removebg.utils.ErrorResponse
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Magic : AppCompatActivity() {
    var photoFile: File? = null
    val CAPTURE_IMAGE_REQUEST = 1
    var mCurrentPhotoPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_magic)
        val button_capture = findViewById<Button>(R.id.button_capture)
        button_capture.setOnClickListener {
            captureImage()
        }
        RemoveBg.init("BLFZG64Vbj1E7DugVz2ERCAr")
        photoFile?.let {
            RemoveBg.from(it, object : RemoveBg.RemoveBgCallback {

            override fun onProcessing() {
                // will be invoked once finished uploading the image
            }

            override fun onUploadProgress(progress: Float) {
                // will be invoked on uploading
            }

            override fun onError(errors: List<ErrorResponse.Error>) {
                // will be invoked if there's any error occurred
            }

            override fun onSuccess(bitmap: Bitmap) {
                // will be invoked when background removed
                displayMessage(baseContext, "Yay")
            }

        })
        }
    }
    private fun captureImage() {

        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0
            )
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                // Create the File where the photo should go
                try {
                    photoFile = createImageFile()
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(
                                this,
                                " com.onesilicondiode.dropme.fileprovider",
                                photoFile!!
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
                    }
                } catch (ex: Exception) {
                    // Error occurred while creating the File
                    displayMessage(baseContext, ex.message.toString())
                }

            } else {
                displayMessage(baseContext, "Null")
            }
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun displayMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val myBitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath)
          //  imageView.setImageBitmap(myBitmap)
        } else {
            displayMessage(baseContext, "Request cancelled or something went wrong.")
        }
    }

}