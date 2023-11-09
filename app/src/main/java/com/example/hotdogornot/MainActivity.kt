package com.example.hotdogornot

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.text.Layout
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.hotdogornot.ui.theme.HotdogOrNotTheme
import java.io.File
import java.io.FileOutputStream


class MainActivity : ComponentActivity() {

    private lateinit var imageClassifier: ImageClassifier


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this, CAMERAX_PERMISSIONS,0
        )


        // Initialize the image classifier
        imageClassifier = ImageClassifier(this, "model-72-52.tflite")

        // Load and classify an image
        val result = imageClassifier.classifyImage("hotdog.jpg")
        Log.d("Result: ", result) // Log the result to the console


        setContent {
            HotdogOrNotTheme {
                val controller = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(
                            CameraController.IMAGE_CAPTURE
                        )
                    }
                }

                // CameraPreview kör kameran
                CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    IconButton(onClick = { takePhoto(controller = controller, context = applicationContext) {} }) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Take photo")
                    }

                }

            }
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)
    }



    private fun takePhoto(
        controller: LifecycleCameraController,
        context: Context,
        onPhotoTaken: (Bitmap) -> Unit
    ) {
        controller.takePicture(
            ContextCompat.getMainExecutor(applicationContext),
            object : OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val bitmap = image.toBitmap()
                    onPhotoTaken(bitmap)

                    classifyCapturedImage(context, bitmap)

                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera", "Could not take photo: ", exception)
                }
            }
        )
    }

    private fun classifyCapturedImage(context: Context, bitmap: Bitmap) {
        val resizedBitmap = resizeBitmap(bitmap,300,300)

        val result = imageClassifier.classifyImageBitmap(resizedBitmap)
        Log.d("ImageClassification", "Result: $result")
        Toast.makeText(context, "$result", Toast.LENGTH_LONG).show()
    }

    companion object{
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )
    }
}
