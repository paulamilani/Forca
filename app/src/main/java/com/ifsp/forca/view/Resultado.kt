package com.ifsp.forca.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ifsp.forca.databinding.ActivityResultadoBinding

class Resultado : AppCompatActivity() {
    private val activityResultadoBinding: ActivityResultadoBinding by lazy {
        ActivityResultadoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityResultadoBinding.root)

        val correctAnswers = intent.getStringArrayListExtra("correctAnswers")
        val wrongAnswers = intent.getStringArrayListExtra("wrongAnswers")

        val acertosStringBuffer = StringBuffer()
        for (index in 0 until (correctAnswers?.size ?: 0)) {
            acertosStringBuffer.append("- " + (correctAnswers?.get(index) ?: "") + "\n")
        }

        val errosStringBuffer = StringBuffer()
        for (index in 0 until (wrongAnswers?.size ?: 0)) {
            errosStringBuffer.append("- " + (wrongAnswers?.get(index) ?: "") + "\n")
        }

        activityResultadoBinding.acertosLabelTv.text = "${correctAnswers?.size} acertos:"
        activityResultadoBinding.errosLabelTv.text = "${wrongAnswers?.size} erros:"
        activityResultadoBinding.totalPalavrasTv.text =
            "${(correctAnswers?.size ?: 0) + (wrongAnswers?.size ?: 0)} palavras"
        activityResultadoBinding.acertosTv.text = acertosStringBuffer.toString()
        activityResultadoBinding.errosTv.text = errosStringBuffer.toString()
        activityResultadoBinding.backBtn.setOnClickListener {
            finish()
        }
        activityResultadoBinding.nextBtn.setOnClickListener {
            val intent = Intent(this, Jogar::class.java)
            startActivity(intent)
            finish()
        }

    }
}