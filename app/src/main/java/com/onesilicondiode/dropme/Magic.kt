package com.onesilicondiode.dropme

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.*
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.esafirm.imagepicker.features.ImagePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.BasePermissionListener
import com.karumi.dexter.listener.single.CompositePermissionListener
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.theapache64.removebg.RemoveBg
import com.theapache64.removebg.utils.ErrorResponse
import com.theapache64.twinkill.logger.info
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

class Magic : AppCompatActivity() {

    private val projectDir by lazy {
        val rootPath = Environment.getExternalStorageDirectory().absolutePath
        File("$rootPath/remove-bg")
    }
    private var outputImage: File? = null
    private var inputImage: File? = null

    @BindView(R.id.iv_input)
    lateinit var ivInput: ImageView

    @BindView(R.id.iv_output)
    lateinit var ivOutput: ImageView

    @BindView(R.id.tv_input_details)
    lateinit var tvInputDetails: TextView

    @BindView(R.id.b_process)
    lateinit var bProcess: View

    @BindView(R.id.tv_progress)
    lateinit var tvProgress: TextView

    @BindView(R.id.pb_progress)
    lateinit var pbProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_magic)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this)
    }
    @OnClick(R.id.b_choose_image, R.id.i_choose_image)
    fun onChooseImageClicked() {
        vibrateDevice()
        info("Choose inputImage clicked")

        ImagePicker.create(this)
                .single()
                .start()
    }

    @OnClick(R.id.iv_input)
    fun onOutputClicked() {
        if (outputImage != null) {
            viewImage(outputImage!!)
        } else {
            toast("Process Image First!")
        }
    }
   // @OnClick(R.id.iv_output)

    private fun viewImage(inputImage: File) {

        val uri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", inputImage)

        Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(this)
        }
    }
    @OnClick(R.id.b_process)
    fun onProcessClicked() {
        if (inputImage != null) {
            vibrateDevice()
            info("Image is ${inputImage!!.path}")
            // Check permission
            checkPermission {

                info("Permission granted")

                // permission granted, compress the inputImage now
                compressImage(inputImage!!) { bitmap ->

                    info("Image compressed")

                    saveImage("${System.currentTimeMillis()}", bitmap) { compressedImage ->

                        info("Compressed inputImage saved to ${compressedImage.absolutePath}, and removing bg...")
                        val compressedImageSize = compressedImage.length() / 1024
                        val originalImageSize = inputImage!!.length() / 1024

                        val finalImage = if (compressedImageSize < originalImageSize) compressedImage else inputImage!!

                        // inputImage saved, now upload
                        RemoveBg.from(finalImage, object : RemoveBg.RemoveBgCallback {

                            override fun onProcessing() {
                                runOnUiThread {
                                    tvProgress.setText("Processing")
                                }
                            }

                            override fun onUploadProgress(progress: Float) {
                                runOnUiThread {
                                    tvProgress.text = "Uploading ${progress.toInt()}%"
                                    pbProgress.progress = progress.toInt()
                                }
                            }

                            override fun onError(errors: List<ErrorResponse.Error>) {
                                runOnUiThread {
                                    val errorBuilder = StringBuilder()
                                    errors.forEach {
                                        errorBuilder.append("${it.title} : ${it.detail} : ${it.code}\n")
                                    }

                                    showErrorAlert(errorBuilder.toString())
                                }
                            }
                            override fun onSuccess(bitmap: Bitmap) {
                                info("background removed from bg , and output is $bitmap")
                                runOnUiThread {
                                    ivInput.setImageBitmap(bitmap)
                                    i_choose_image.visibility = View.GONE
                                    // ivOutput.visibility = View.VISIBLE
                                    // Save output image
                                    saveImage("${inputImage!!.name}-DropMe", bitmap) {
                                        outputImage = it
                                    }
                                }
                            }

                        })
                    }
                }
            }

        } else {
            toast("No Image")
        }
    }
    private fun compressImage(image: File, onLoaded: (bitmap: Bitmap) -> Unit) {

        Glide.with(this)
                .asBitmap()
                .load(image)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        onLoaded(resource)
                    }
                })
    }
    private fun saveImage(fileName: String, bitmap: Bitmap, onSaved: (file: File) -> Unit) {

        // Create project dir
        if (!projectDir.exists()) {
            projectDir.mkdir()
        }

        // Create inputImage file
        val imageFile = File("$projectDir/$fileName.jpg")
        imageFile.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            out.flush()
        }

        onSaved(imageFile)
    }
    /**
     * To show an alert message with title 'Error'
     */
    private fun showErrorAlert(message: String) {
        AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .create()
                .show()
    }
    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private fun checkPermission(onPermissionChecked: () -> Unit) {

        val deniedListener = DialogOnDeniedPermissionListener.Builder.withContext(this)
                .withTitle("Permission Required")
                .withMessage("Dedo Shaanti Se")
                .withButtonText("Ok")
                .build()

        val permissionListener = object : BasePermissionListener() {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                onPermissionChecked()
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                toast("Permission Denied")
            }
        }

        val listener = CompositePermissionListener(permissionListener, deniedListener)

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(listener)
                .check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            // IMAGE PICKED!!
            val imagePicked = ImagePicker.getFirstImageOrNull(data)

            if (imagePicked != null) {

                this.inputImage = File(imagePicked.path)

                ivInput.visibility = View.VISIBLE

                Glide.with(this)
                        .load(this.inputImage)
                        .into(ivInput)

                // Showing process button
                // Showing process button
                bProcess.visibility = View.VISIBLE
                ivOutput.visibility = View.INVISIBLE

            } else {
                toast("No Image Selected")
            }

            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    private fun vibrateDevice() {
        val v3: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v3.vibrate(VibrationEffect.createOneShot(28, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v3.vibrate(25)
        }
    }
}