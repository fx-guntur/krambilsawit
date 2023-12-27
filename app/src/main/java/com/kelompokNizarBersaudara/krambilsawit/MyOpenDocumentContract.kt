package com.kelompokNizarBersaudara.krambilsawit

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MyOpenDocumentContract : ActivityResultContract<Array<String>, Uri?>() {
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
        if (resultCode == RESULT_OK) {
            return intent?.data
        }
        return null
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }
}