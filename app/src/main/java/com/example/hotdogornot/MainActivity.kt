package com.example.hotdogornot

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.hotdogornot.ui.theme.HotdogOrNotTheme

class MainActivity : ComponentActivity() {

    private lateinit var imageClassifier: ImageClassifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the image classifier
        imageClassifier = ImageClassifier(this, "model-72-52.tflite")

        // Load and classify an image
        val imagePath = "nothotdog.jpg" // Replace with your image path in the assets folder
        val result = imageClassifier.classifyImage(imagePath)
        Log.d("ImageClassification ", result) // Log the result to the console

        setContent {
            HotdogOrNotTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                }
            }
        }
    }
}
