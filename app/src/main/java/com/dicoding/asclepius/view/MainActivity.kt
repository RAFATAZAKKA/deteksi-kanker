package com.dicoding.asclepius.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private val REQUEST_CODE_PICK_IMAGE = 100
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ImageClassifierHelper
        imageClassifierHelper = ImageClassifierHelper(this)

        // Mengatur tombol untuk membuka galeri
        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        // Mengatur tombol untuk analisis gambar
        binding.analyzeButton.setOnClickListener {
            analyzeImage()
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            currentImageUri = data?.data
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            binding.previewImageView.setImageURI(uri)
            binding.previewImageView.tag = uri
            showToast("Gambar berhasil ditampilkan.")
        } ?: showToast("Gagal menampilkan gambar.")
    }

    private fun analyzeImage() {
        val imageUri = binding.previewImageView.tag as? Uri
        if (imageUri != null) {
            // Panggil ImageClassifierHelper untuk klasifikasi
            val result = imageClassifierHelper.classifyStaticImage(imageUri)
            if (result != null) {
                showClassificationResult(result)
            } else {
                showToast("Gagal melakukan klasifikasi.")
            }
        } else {
            showToast("Pilih gambar terlebih dahulu.")
        }
    }

    private fun showClassificationResult(result: FloatArray?) {
        result?.let {
            val prediction = if (it[0] > it[1]) "Kanker" else "Bukan Kanker"
            val confidence = if (it[0] > it[1]) it[0] * 100 else it[1] * 100

            // Pindah ke ResultActivity dengan hasil prediksi
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("PREDICTION", prediction)
                putExtra("CONFIDENCE", confidence)
                putExtra("IMAGE_URI", currentImageUri.toString())
                putExtra("CLASSIFICATION_RESULT", it)
            }
            startActivity(intent)
        } ?: showToast("Gagal melakukan klasifikasi.")
    }


    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}