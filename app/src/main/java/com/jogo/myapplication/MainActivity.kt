package com.jogo.myapplication

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var editWeight: EditText
    private lateinit var seekBarHeight: SeekBar
    private lateinit var buttonClear: Button
    private lateinit var buttonCalculate: Button
    private lateinit var textHeightValue: TextView
    private lateinit var textResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components
        editWeight = findViewById<EditText>(R.id.edit_weight).apply {
            contentDescription = getString(R.string.weight_input_description)
        }
        seekBarHeight = findViewById<SeekBar>(R.id.seekbar_height).apply {
            contentDescription = getString(R.string.height_seekbar_description)
            max = 250 // Max height: 250 cm
            progress = 150 // Default height: 150 cm
        }
        buttonClear = findViewById<Button>(R.id.button_clear).apply {
            contentDescription = getString(R.string.clear_button_description)
        }
        buttonCalculate = findViewById<Button>(R.id.button_calculate).apply {
            contentDescription = getString(R.string.calculate_button_description)
        }
        textHeightValue = findViewById(R.id.text_height_value)
        textResult = findViewById(R.id.text_result)

        // Initialize SeekBar display
        textHeightValue.text = getString(R.string.height_value, seekBarHeight.progress)
        textHeightValue.visibility = View.VISIBLE

        // Update height value dynamically, enforce minimum 50 cm
        seekBarHeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val adjustedProgress = progress.coerceAtLeast(50) // Enforce min 50 cm
                if (progress != adjustedProgress) {
                    seekBar?.progress = adjustedProgress
                }
                textHeightValue.text = getString(R.string.height_value, adjustedProgress)
                textHeightValue.visibility = View.VISIBLE
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Calculate button click
        buttonCalculate.setOnClickListener {
            calculateBMI()
        }

        // Clear button click
        buttonClear.setOnClickListener {
            clearInputs()
        }
    }

    private fun calculateBMI() {
        val weightStr = editWeight.text.toString().trim()
        val heightCm = seekBarHeight.progress

        if (TextUtils.isEmpty(weightStr) || heightCm <= 0) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val weight = weightStr.toDouble()
            val height = heightCm / 100.0 // Convert cm to meters

            if (weight <= 0 || height <= 0) {
                Toast.makeText(this, R.string.error_invalid_values, Toast.LENGTH_SHORT).show()
                return
            }

            val bmi = weight / (height * height)
            val classification = when {
                bmi < 18.5 -> getString(R.string.bmi_underweight)
                bmi < 25 -> getString(R.string.bmi_normal)
                bmi < 30 -> getString(R.string.bmi_overweight)
                else -> getString(R.string.bmi_obese)
            }

            textResult.text = getString(R.string.bmi_result, bmi, classification)
            textResult.visibility = View.VISIBLE

        } catch (_: NumberFormatException) {
            Toast.makeText(this, R.string.error_invalid_input, Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearInputs() {
        editWeight.text.clear()
        seekBarHeight.progress = 150
        textHeightValue.text = getString(R.string.height_value, 150)
        textHeightValue.visibility = View.GONE
        textResult.visibility = View.GONE
        textResult.text = getString(R.string.result_placeholder)
    }
}