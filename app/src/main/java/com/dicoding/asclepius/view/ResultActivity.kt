package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper


class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan data hasil dari intent
        val prediction = intent.getStringExtra("PREDICTION")
        val confidence = intent.getFloatExtra("CONFIDENCE", 0f)
        val imageUriString = intent.getStringExtra("IMAGE_URI")
        val imageUri = Uri.parse(imageUriString)

        // Menampilkan hasil prediksi, confidence score, dan gambar
        if (confidence > 58) {
            binding.resultText.text = "Cancer."
        } else {
            binding.resultText.text = "Non Cancer."
        }

        binding.resultConfidence.text = String.format("Confidence: %.0f%%", confidence)

        // Menampilkan gambar
        binding.resultImage.setImageURI(imageUri)
    }
}