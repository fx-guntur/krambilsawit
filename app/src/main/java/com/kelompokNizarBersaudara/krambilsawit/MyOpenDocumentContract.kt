package com.kelompokNizarBersaudara.krambilsawit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import android.os.Environment
import java.util.Date

class MyOpenDocumentContract(private val context: Context) : ActivityResultContract<Array<String>, Uri?>() {
    private var currentPhotoPath: String? = null
    override fun createIntent(context: Context, input: Array<String>): Intent {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val documentIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        documentIntent.addCategory(Intent.CATEGORY_OPENABLE)
        input.forEach { mimeType ->
            documentIntent.type = mimeType
        }

        val chooserIntent = Intent.createChooser(documentIntent, "Pilih Sumber Gambar")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(captureIntent))
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }

        return chooserIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode == Activity.RESULT_OK) {
            if (intent?.action == MediaStore.ACTION_IMAGE_CAPTURE) {
                if (currentPhotoPath != null) {
                    val file = File(currentPhotoPath!!)
                    if (file.exists()) {
                        return FileProvider.getUriForFile(
                            context,
                            "com.kelompokNizarBersaudara.krambilsawit.fileprovider",
                            file
                        )
                    }
                }
            } else if (intent?.data != null) {
                return intent.data
            }
        }

        return null
    }

    private fun dispatchTakePictureIntent(activity: Activity) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        takePictureIntent.resolveActivity(activity.packageManager)?.also {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    activity,
                    "com.kelompokNizarBersaudara.krambilsawit.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                activity.startActivityForResult(takePictureIntent, CAMERA_PERMISSION_REQUEST_CODE)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val imageFile = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
        currentPhotoPath = imageFile.absolutePath
        return imageFile
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }
}