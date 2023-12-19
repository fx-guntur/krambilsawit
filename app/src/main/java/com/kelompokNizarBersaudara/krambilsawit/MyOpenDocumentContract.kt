package com.kelompokNizarBersaudara.krambilsawit

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

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
        return chooserIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode == RESULT_OK) {
            return intent?.data
        }
        return null
    }
}