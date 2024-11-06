package com.dicoding.asclepius.helper

import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

//class ImageClassifierHelper(
//    private val context: Context,
//    private val modelName: String = "cancer_classification.tflite"
//) {
//
//    private lateinit var interpreter: Interpreter
//
//    init {
//        setupImageClassifier()
//    }
//
//    private fun setupImageClassifier() {
//        try {
//            val assetFileDescriptor = context.assets.openFd("cancer_classification.tflite")
//            val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
//            val fileChannel = fileInputStream.channel
//            val startOffset = assetFileDescriptor.startOffset
//            val declaredLength = assetFileDescriptor.declaredLength
//            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//            interpreter = Interpreter(modelBuffer)
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Toast.makeText(context, "Gagal memuat model.", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    fun classifyStaticImage(imageUri: Uri): FloatArray? {
//        // Mengambil gambar dari URI dan mengubahnya menjadi Bitmap
//        val inputStream = context.contentResolver.openInputStream(imageUri)
//        var bitmap = BitmapFactory.decodeStream(inputStream)
//        inputStream?.close()
//
//        // Resize gambar ke ukuran yang sesuai dengan model (224x224)
//        bitmap = bitmap?.let { resizeBitmapToModelSize(it, 224, 224) }
//
//        return bitmap?.let { classifyBitmap(it) }
//    }
//
//    private fun classifyBitmap(bitmap: Bitmap): FloatArray? {
//        val inputBuffer = convertBitmapToByteBuffer(bitmap)
//        val outputBuffer = Array(1) { FloatArray(2) }  // Output untuk dua kelas
//
//        interpreter.run(inputBuffer, outputBuffer)  // Jalankan model dengan input
//
//        return outputBuffer[0]  // Mengembalikan hasil prediksi
//    }
//
//    private fun resizeBitmapToModelSize(bitmap: Bitmap, width: Int, height: Int): Bitmap {
//        return Bitmap.createScaledBitmap(bitmap, width, height, true)
//    }
//
//    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
//        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)  // 224x224 dengan 3 channel
//        byteBuffer.order(ByteOrder.nativeOrder())
//
//        // Mengubah ukuran gambar sesuai yang diperlukan oleh model
//        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
//        val intValues = IntArray(224 * 224)
//        resizedBitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224)
//
//        for (pixel in intValues) {
//            byteBuffer.putFloat(((pixel shr 16) and 0xFF) / 255f)  // Red
//            byteBuffer.putFloat(((pixel shr 8) and 0xFF) / 255f)    // Green
//            byteBuffer.putFloat((pixel and 0xFF) / 255f)            // Blue
//        }
//
//        return byteBuffer
//    }
//}


class ImageClassifierHelper(
    private val context: Context,
    private val modelName: String = "cancer_classification.tflite"

) {

    private lateinit var interpreter: Interpreter

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        try {
            val assetFileDescriptor = context.assets.openFd("cancer_classification.tflite")
            val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
            val fileChannel = fileInputStream.channel
            val startOffset = assetFileDescriptor.startOffset
            val declaredLength = assetFileDescriptor.declaredLength
            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            interpreter = Interpreter(modelBuffer)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal memuat model.", Toast.LENGTH_SHORT).show()
        }
    }

    fun classifyStaticImage(imageUri: Uri): FloatArray? {
        // Mengambil gambar dari URI dan mengubahnya menjadi Bitmap
        val inputStream = context.contentResolver.openInputStream(imageUri)
        var bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // Resize gambar ke ukuran yang sesuai dengan model (224x224)
        bitmap = bitmap?.let { resizeBitmapToModelSize(it, 224, 224) }

        return bitmap?.let { classifyBitmap(it) }
    }

    private fun classifyBitmap(bitmap: Bitmap): FloatArray? {
        val inputBuffer = convertBitmapToByteBuffer(bitmap)
        val outputBuffer = Array(1) { FloatArray(2) }  // Output untuk dua kelas

        interpreter.run(inputBuffer, outputBuffer)  // Jalankan model dengan input

        return outputBuffer[0]  // Mengembalikan hasil prediksi
    }

    private fun resizeBitmapToModelSize(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)  // 224x224 dengan 3 channel
        byteBuffer.order(ByteOrder.nativeOrder())

        // Mengubah ukuran gambar sesuai yang diperlukan oleh model
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val intValues = IntArray(224 * 224)
        resizedBitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224)

        for (pixel in intValues) {
            byteBuffer.putFloat(((pixel shr 16) and 0xFF) / 255f)  // Red
            byteBuffer.putFloat(((pixel shr 8) and 0xFF) / 255f)    // Green
            byteBuffer.putFloat((pixel and 0xFF) / 255f)            // Blue
        }

        return byteBuffer
    }
}