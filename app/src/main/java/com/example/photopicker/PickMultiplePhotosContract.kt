package com.example.photopicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi

class PickMultiplePhotosContract:  ActivityResultContract<Unit, List<Uri>>() {

    @RequiresApi(33)
    override fun createIntent(context: Context, input: Unit): Intent {

        return if (PhotoPickerAvailabilityChecker.isPhotoPickerAvailable()) {
            Intent(MediaStore.ACTION_PICK_IMAGES)
                .putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, MediaStore.getPickImagesMaxLimit())
        } else {
            Intent(Intent.ACTION_OPEN_DOCUMENT)
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }.apply { type = MIME_TYPE_IMAGE }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.clipData?.let { clipData ->
            val selectedUris: LinkedHashSet<Uri> = LinkedHashSet()

            for (index in 0 until clipData.itemCount) {
                val uri: Uri = clipData.getItemAt(index).uri
                selectedUris.add(uri)
            }

            ArrayList(selectedUris)
        } ?: emptyList()
    }
}

private const val MIME_TYPE_IMAGE = "image/*"