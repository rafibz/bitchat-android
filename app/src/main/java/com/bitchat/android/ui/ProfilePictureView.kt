package com.bitchat.android.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bitchat.android.ui.DataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Profile Picture View that displays user's profile picture or a blank avatar
 */
@Composable
fun ProfilePictureView(
    profilePicturePath: String? = null,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
    showBorder: Boolean = true
) {
    val context = LocalContext.current
    val dataManager = remember { DataManager(context) }
    
    var bitmap by remember(profilePicturePath) { mutableStateOf<Bitmap?>(null) }
    
    // Load profile picture if path is provided
    LaunchedEffect(profilePicturePath) {
        if (profilePicturePath != null) {
            val imageFile = File(profilePicturePath)
            if (imageFile.exists()) {
                bitmap = withContext(Dispatchers.IO) {
                    try {
                        BitmapFactory.decodeFile(profilePicturePath)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        }
    }
    
    Box(
        modifier = modifier
            .size(size)
            .background(Color.LightGray, CircleShape)
            .then(
                if (showBorder) {
                    Modifier.border(2.dp, Color.Gray, CircleShape)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            bitmap != null -> {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(size),
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                // Show blank avatar icon
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Blank Avatar",
                    modifier = Modifier.size(size * 0.6f),
                    tint = Color.Gray
                )
            }
        }
    }
}

