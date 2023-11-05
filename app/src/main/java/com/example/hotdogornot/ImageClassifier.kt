package com.example.hotdogornot

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageClassifier(private val context: Context, private val modelPath: String) {

    private val tflite: Interpreter

    init {
        val tfliteOptions = Interpreter.Options()
        tflite = Interpreter(loadModelFile(modelPath, context), tfliteOptions)
    }


    fun classifyImage(imagePath: String): String {

        if (!imageExists(imagePath)) {
            return "Image not found"
        }

        // Load and preprocess the image
        val bitmap = loadAndPreprocessImage(imagePath, context)
        val result = Array(1) { FloatArray(1) } // Update the shape to match the model's output
        tflite.run(bitmap, result)

        // Interpret the prediction
        val hotDogConfidence = result[0][0]

        if (hotDogConfidence < 0.5) return "It's a hot dog!"
        else return "It's not a hot dog!"

    }


    private fun imageExists(imagePath: String): Boolean {
        return try {
            context.assets.open(imagePath).close()
            true
        } catch (e: IOException) {
            false
        }
    }

    private fun loadModelFile(modelPath: String, context: Context): ByteBuffer {
        val assetManager = context.assets

        if (!modelExists(modelPath, context)) {
            throw IOException("Model file not found")
        }

        val modelDescriptor = assetManager.openFd(modelPath)
        val modelBuffer = modelDescriptor.createInputStream().use { input ->
            val length = modelDescriptor.length.toInt()
            val modelData = ByteArray(length)
            input.read(modelData)

            val modelBuffer = ByteBuffer.allocateDirect(length)
            modelBuffer.order(ByteOrder.nativeOrder())
            modelBuffer.put(modelData)
            modelBuffer.rewind()

            modelBuffer
        }

        return modelBuffer
    }


    private fun modelExists(modelPath: String, context: Context): Boolean {
        return try {
            context.assets.openFd(modelPath).close()
            true
        } catch (e: IOException) {
            false
        }
    }

    private fun loadAndPreprocessImage(imagePath: String, context: Context): ByteBuffer {
        // Load the image
        val imageStream = context.assets.open(imagePath)
        val bitmap = BitmapFactory.decodeStream(imageStream)

        // Resize the image to 150x150
        val targetSize = 150
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetSize, targetSize, false)

        val inputBuffer = ByteBuffer.allocateDirect(4 * targetSize * targetSize * 3)
        inputBuffer.order(ByteOrder.nativeOrder())

        for (x in 0 until targetSize) {
            for (y in 0 until targetSize) {
                val pixel = scaledBitmap.getPixel(x, y)
                val red = Color.red(pixel).toFloat() / 255.0f
                val green = Color.green(pixel).toFloat() / 255.0f
                val blue = Color.blue(pixel).toFloat() / 255.0f
                inputBuffer.putFloat(red)
                inputBuffer.putFloat(green)
                inputBuffer.putFloat(blue)
            }
        }

        return inputBuffer
    }

}
