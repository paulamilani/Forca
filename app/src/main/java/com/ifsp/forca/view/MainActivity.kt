package com.ifsp.forca.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ifsp.forca.R
import com.ifsp.forca.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        activityMainBinding.jogarBtn.setOnClickListener {
            val intent: Intent = Intent(this, Jogar::class.java)
            startActivity(intent)
        }

        activityMainBinding.configBtn.setOnClickListener {
            val intent: Intent = Intent(this, Config::class.java)
            startActivity(intent)
        }
    }
}