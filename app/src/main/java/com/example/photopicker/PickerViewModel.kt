package com.example.photopicker

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.InputStream


class PickerViewModel(application: Application) : AndroidViewModel(application) {

    data class ViewState(
        val imageBitmap: Bitmap? = null,
        val imageUri: Uri? = null,
        val multipleUris: MutableList<Uri>? = null
    )

    private val _viewState = MutableStateFlow(ViewState())
    val viewState = _viewState.asStateFlow()

    private val _uriList: MutableStateFlow<MutableList<Uri>> = MutableStateFlow(mutableListOf())
    val uriList = _uriList.asStateFlow()

    // By calling contentResolver.takePersistableUriPermission(..)
    // with the respective Uri and the required permission flags,
    // we can extend the setImageUri() function in the following way
    // to persist the access to the receiving Uri
    fun setImageUri(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            getContext().contentResolver.let { contentResolver: ContentResolver ->
                val readUriPermission: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, readUriPermission)
                contentResolver.openInputStream(uri)?.use { inputStream: InputStream ->
                    _viewState.update { currentState: ViewState ->
                        currentState.copy(
                            imageBitmap = BitmapFactory.decodeStream(inputStream),
                            imageUri = uri
                        )
                    }
                }
            }
        }
    }

    fun setImageUris(uris: MutableList<Uri>) {
        viewModelScope.launch(Dispatchers.IO) {
            getContext().contentResolver.let { contentResolver: ContentResolver ->
                val readUriPermission: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                for (uri in uris) {
                    contentResolver.takePersistableUriPermission(uri, readUriPermission)
                }
                _uriList.update { uris }
            }
        }
    }

    private fun getContext(): Context = getApplication<Application>().applicationContext
}