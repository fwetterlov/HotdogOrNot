package com.example.hotdogornot

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.hotdogornot.ui.theme.HotdogOrNotTheme

class MainActivity : ComponentActivity() {

    private lateinit var imageClassifier: ImageClassifier
    private lateinit var vibrator: Vibrator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this, CAMERAX_PERMISSIONS,0
        )


        // Initialize the image classifier
        imageClassifier = ImageClassifier(this, "model-72-52.tflite")
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        
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
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter){
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                        horizontalArrangement = Arrangement.Center) {
                        IconButton(onClick = { takePhoto(controller = controller, context = applicationContext) {} }) {
                            Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Take photo")
                        }
                    }
                }

            }
        }
    }

    private fun vibrate(duration: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // For older devices without VibrationEffect support
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }


    private fun takePhoto(
        controller: LifecycleCameraController,
        context: Context,
        onPhotoTaken: (Bitmap) -> Unit
    ) {
        vibrate(100)
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
        val result = imageClassifier.classifyImageBitmap(bitmap)
        Log.d("ImageClassification", "Result: $result")
        Toast.makeText(context, "$result", Toast.LENGTH_LONG).show()
    }

    companion object{
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )
    }
}
