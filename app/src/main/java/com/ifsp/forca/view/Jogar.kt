package com.ifsp.forca.view

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ifsp.forca.databinding.ActivityJogarBinding
import com.ifsp.forca.viewModel.ForcaViewModel
import java.text.Normalizer

class Jogar : AppCompatActivity() {
    private val activityJogarBinding: ActivityJogarBinding by lazy {
        ActivityJogarBinding.inflate(layoutInflater)
    }

    private var keyboardEnabled = false
    private var word: String = ""
    private var ganhouRound = false

    private lateinit var forcaViewModel: ForcaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityJogarBinding.root)

        forcaViewModel = ViewModelProvider
            .AndroidViewModelFactory(this.application)
            .create(ForcaViewModel::class.java)

        addKeyboardListeners()

        activityJogarBinding.nextBtn.setOnClickListener {
            forcaViewModel.finishRound(ganhouRound)
        }

        startGame()
        observeWord()
        observeIdentifiers()
        getTotalRounds()
        observeCurrentRound()
        observeCurrentLevel()
        observeAttempts()
        observeGameEnded()
    }

    fun startGame() {
        forcaViewModel.startGame()
    }

    fun CharSequence.unaccent(): String {
        val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }

    fun guess(key: String) {
        forcaViewModel.guess(key)
        val stringBuilder = StringBuilder()
        stringBuilder.append(activityJogarBinding.letrasTv.text)
        if (activityJogarBinding.letrasTv.text.length > 0) {
            stringBuilder.append(" - ")
        }
        stringBuilder.append(key)



        if (word.uppercase().contains(key.uppercase())) {
            Toast.makeText(this, "Alternativa correta", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Alternativa incorreta", Toast.LENGTH_SHORT).show()
        }

        for (index in 0 until word.length) {
            var guessingWord = activityJogarBinding.palavraTv.text.toString()
            if (word[index].toString().unaccent().uppercase() == key.uppercase()) {
                var before = " "
                var after = ""
                if (index > 0) {
                    before = guessingWord.substring(0, index * 2 + 1)
                }
                if (index < word.length - 1) {
                    after = guessingWord.substring((index + 1) * 2)
                }

                activityJogarBinding.palavraTv.text = "${before}${key}${after}"
                if (!activityJogarBinding.palavraTv.text.contains("_")) {
                    activityJogarBinding.botoesLL.visibility = View.VISIBLE
                    activityJogarBinding.tecladoCl.visibility = View.GONE
                    ganhouRound = true
                    Toast.makeText(this, "Você ganhou esta rodada", Toast.LENGTH_SHORT).show()
                }
            }
        }

        activityJogarBinding.letrasTv.text = stringBuilder.toString()
    }

    fun observeWord() {
        forcaViewModel.wordMld.observe(this) { updatedWord ->
            keyboardEnabled = true
            word = updatedWord.palavra
            val stringBuilder = StringBuilder()

            for (index in 0 until updatedWord.letra) {
                stringBuilder.append(" _")
            }
            runOnUiThread {
                activityJogarBinding.palavraTv.text = stringBuilder.toString()
                activityJogarBinding.letrasTv.text = ""
                activityJogarBinding.botoesLL.visibility = View.GONE
                activityJogarBinding.tecladoCl.visibility = View.VISIBLE
                ganhouRound = false
                enabledAllKeyboardKeys()
            }
        }
    }

    fun observeIdentifiers() {
        forcaViewModel.identifiersMld.observe(this) { identifiers ->
            forcaViewModel.generateRoundIdentifiers()
            forcaViewModel.nextRound()
        }
    }

    fun getTotalRounds() {
        val total = forcaViewModel.getRounds()
        activityJogarBinding.totalRodadasTv.text = total.toString()
    }

    fun observeCurrentRound() {
        forcaViewModel.currentRoundMdl.observe(this) { currentRound ->
            runOnUiThread {
                activityJogarBinding.rodadaAtualTv.text = "Rodada $currentRound de "
                val total = activityJogarBinding.totalRodadasTv.text.toString().toInt()
                if (currentRound < total) {
                    activityJogarBinding.nextBtn.text = "Próxima rodada"
                } else {
                    activityJogarBinding.nextBtn.text = "Ver resultados"
                }
            }
        }
    }

    fun observeCurrentLevel() {
        val nivel = forcaViewModel.getDifficulty()
        activityJogarBinding.nivelAtualTv.text = nivel.toString()

    }

    fun observeAttempts() {
        forcaViewModel.attemptsMdl.observe(this) { attempts ->
            updateAttempts(attempts)
        }
    }

    fun updateAttempts(remainingAttempts: Int) {

        activityJogarBinding.cabecaTv.paintFlags = 0
        activityJogarBinding.troncoTv.paintFlags = 0
        activityJogarBinding.bracoDireitoTv.paintFlags = 0
        activityJogarBinding.bracoEsquerdoTv.paintFlags = 0
        activityJogarBinding.pernaDireitaTv.paintFlags = 0
        activityJogarBinding.pernaEsquerdaTv.paintFlags = 0

        if (remainingAttempts < 6) {
            activityJogarBinding.cabecaTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (remainingAttempts < 5) {
            activityJogarBinding.troncoTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (remainingAttempts < 4) {
            activityJogarBinding.bracoDireitoTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (remainingAttempts < 3) {
            activityJogarBinding.bracoEsquerdoTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (remainingAttempts < 2) {
            activityJogarBinding.pernaDireitaTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (remainingAttempts < 1) {
            activityJogarBinding.pernaEsquerdaTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            activityJogarBinding.botoesLL.visibility = View.VISIBLE
            activityJogarBinding.tecladoCl.visibility = View.GONE
            Toast.makeText(this, "Você perdeu esta rodada", Toast.LENGTH_SHORT).show()
        }
    }

    fun addKeyboardListeners() {
        with(activityJogarBinding) {
            letterABtn.setOnClickListener { pressKey("A") }
            letterBBtn.setOnClickListener { pressKey("B") }
            letterCBtn.setOnClickListener { pressKey("C") }
            letterDBtn.setOnClickListener { pressKey("D") }
            letterEBtn.setOnClickListener { pressKey("E") }
            letterFBtn.setOnClickListener { pressKey("F") }
            letterGBtn.setOnClickListener { pressKey("G") }
            letterHBtn.setOnClickListener { pressKey("H") }
            letterIBtn.setOnClickListener { pressKey("I") }
            letterJBtn.setOnClickListener { pressKey("J") }
            letterKBtn.setOnClickListener { pressKey("K") }
            letterLBtn.setOnClickListener { pressKey("L") }
            letterMBtn.setOnClickListener { pressKey("M") }
            letterNBtn.setOnClickListener { pressKey("N") }
            letterOBtn.setOnClickListener { pressKey("O") }
            letterPBtn.setOnClickListener { pressKey("P") }
            letterQBtn.setOnClickListener { pressKey("Q") }
            letterRBtn.setOnClickListener { pressKey("R") }
            letterSBtn.setOnClickListener { pressKey("S") }
            letterTBtn.setOnClickListener { pressKey("T") }
            letterUBtn.setOnClickListener { pressKey("U") }
            letterVBtn.setOnClickListener { pressKey("V") }
            letterWBtn.setOnClickListener { pressKey("W") }
            letterXBtn.setOnClickListener { pressKey("X") }
            letterYBtn.setOnClickListener { pressKey("Y") }
            letterZBtn.setOnClickListener { pressKey("Z") }
        }
    }

    fun enabledAllKeyboardKeys() {
        with(activityJogarBinding) {
            letterABtn.isEnabled = true
            letterBBtn.isEnabled = true
            letterCBtn.isEnabled = true
            letterDBtn.isEnabled = true
            letterEBtn.isEnabled = true
            letterFBtn.isEnabled = true
            letterGBtn.isEnabled = true
            letterHBtn.isEnabled = true
            letterIBtn.isEnabled = true
            letterJBtn.isEnabled = true
            letterKBtn.isEnabled = true
            letterLBtn.isEnabled = true
            letterMBtn.isEnabled = true
            letterNBtn.isEnabled = true
            letterOBtn.isEnabled = true
            letterPBtn.isEnabled = true
            letterQBtn.isEnabled = true
            letterRBtn.isEnabled = true
            letterSBtn.isEnabled = true
            letterTBtn.isEnabled = true
            letterUBtn.isEnabled = true
            letterVBtn.isEnabled = true
            letterWBtn.isEnabled = true
            letterXBtn.isEnabled = true
            letterYBtn.isEnabled = true
            letterZBtn.isEnabled = true
        }
    }

    fun disableKey(key: String) {
        with(activityJogarBinding) {
            when (key) {
                "A" -> letterABtn.isEnabled = false
                "B" -> letterBBtn.isEnabled = false
                "C" -> letterCBtn.isEnabled = false
                "D" -> letterDBtn.isEnabled = false
                "E" -> letterEBtn.isEnabled = false
                "F" -> letterFBtn.isEnabled = false
                "G" -> letterGBtn.isEnabled = false
                "H" -> letterHBtn.isEnabled = false
                "I" -> letterIBtn.isEnabled = false
                "J" -> letterJBtn.isEnabled = false
                "K" -> letterKBtn.isEnabled = false
                "L" -> letterLBtn.isEnabled = false
                "M" -> letterMBtn.isEnabled = false
                "N" -> letterNBtn.isEnabled = false
                "O" -> letterOBtn.isEnabled = false
                "P" -> letterPBtn.isEnabled = false
                "Q" -> letterQBtn.isEnabled = false
                "R" -> letterRBtn.isEnabled = false
                "S" -> letterSBtn.isEnabled = false
                "T" -> letterTBtn.isEnabled = false
                "U" -> letterUBtn.isEnabled = false
                "V" -> letterVBtn.isEnabled = false
                "W" -> letterWBtn.isEnabled = false
                "X" -> letterXBtn.isEnabled = false
                "Y" -> letterYBtn.isEnabled = false
                "Z" -> letterZBtn.isEnabled = false
            }
        }
    }

    fun pressKey(key: String) {
        if (keyboardEnabled) {
            disableKey(key)
            guess(key)
        }
    }

    fun observeGameEnded() {
        forcaViewModel.gameEndedMdl.observe(this) { gameEnded ->
            if (gameEnded) {
                val intent = Intent(this, Resultado::class.java)
                val wrongAnswers = forcaViewModel.getWrongAnswers()
                val correctAnswers = forcaViewModel.getCorrectAnswers()

                intent.putStringArrayListExtra("correctAnswers", ArrayList(correctAnswers))
                intent.putStringArrayListExtra("wrongAnswers", ArrayList(wrongAnswers))
                startActivity(intent)
                finish()
            }
        }
    }
}