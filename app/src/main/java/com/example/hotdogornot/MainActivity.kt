package com.example.hotdogornot

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotdogornot.ui.theme.HotdogOrNotTheme

class MainActivity : ComponentActivity() {

    private lateinit var imageClassifier: ImageClassifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the image classifier
        imageClassifier = ImageClassifier(this, "model-72-52.tflite")
        // Load and classify an image
        val result = imageClassifier.classifyImage("hotdog.jpg")
        Log.d("Result: ", result) // Log the result to the console

        setContent {
            HotdogOrNotTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Greeting("Check the logcat for classification!")

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column {
        Spacer(modifier = Modifier.height(64.dp))
        Text(
            text = "Hello $name!",
            modifier = modifier.then(Modifier.padding(8.dp)), // Add padding to the Text
            fontSize = 24.sp, // Set the font size to 24sp (you can adjust this as needed)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HotdogOrNotTheme {
        Greeting("Testing")
    }
}
