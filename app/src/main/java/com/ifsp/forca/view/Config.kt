package com.ifsp.forca.view

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ifsp.forca.databinding.ActivityConfigBinding
import com.ifsp.forca.viewModel.ForcaViewModel

class Config : AppCompatActivity() {
    private val activityConfigBinding: ActivityConfigBinding by lazy {
        ActivityConfigBinding.inflate(layoutInflater)
    }

    private lateinit var forcaViewModel: ForcaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityConfigBinding.root)

        forcaViewModel = ViewModelProvider
            .AndroidViewModelFactory(this.application)
            .create(ForcaViewModel::class.java)

        getInitialValues()

        activityConfigBinding.dfSb.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, value: Int, p2: Boolean) {
                forcaViewModel.setDifficulty(value)
                runOnUiThread {
                    activityConfigBinding.dificuldadeMostra.text = "Dificuldade: ${value}"
                    activityConfigBinding.dfSb.progress = value
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //
            }
        })

        activityConfigBinding.roundsSb.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, value: Int, p2: Boolean) {
                forcaViewModel.setTotalRounds(value)
                runOnUiThread {
                    activityConfigBinding.roundsLabelTv.text = "Rodadas: ${value}"
                    activityConfigBinding.roundsSb.progress = value
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //
            }
        })
    }

    fun getInitialValues() {
        val initialDifficulty = forcaViewModel.getDifficulty()
        val initialRounds = forcaViewModel.getRounds()

        activityConfigBinding.roundsLabelTv.text = "Rodadas: ${initialRounds}"
        activityConfigBinding.roundsSb.progress = initialRounds!!

        activityConfigBinding.dificuldadeMostra.text = "Dificuldade: ${initialDifficulty}"
        activityConfigBinding.dfSb.progress = initialDifficulty!!

    }
}