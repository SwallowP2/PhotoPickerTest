package com.example.photopicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.annotation.RequiresApi

private const val MIME_TYPE_IMAGE = "image/*"

class PickSinglePhotoContract : ActivityResultContract<Void?, Uri?>() {

    //because we know all our parameters, we don’t need any input parameters.
    // Therefore we set the input type of our ActivityResultContract to Unit
    @RequiresApi(33)
    override fun createIntent(context: Context, input: Void?): Intent {

        return Intent(if (isPhotoPickerAvailable()) {
            Intent(MediaStore.ACTION_PICK_IMAGES)
        } else {
            //if the Photo Picker is not supported,
            // we stick to the ordinary implementation and use the Intent.ACTION_OPEN_DOCUMENT
            // which will launch the system’s file browser to pick our image.
            Intent(Intent.ACTION_OPEN_DOCUMENT)
        }).apply { type = MIME_TYPE_IMAGE }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.data
    }
}