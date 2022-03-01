package com.example.counterbutton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.counterbutton.customview.attrs.Dimension
import com.example.counterbutton.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.apply {
            btnLoadingTest.setOnClickListener {
                btnCounterButtonStandard.isLoading = !btnCounterButtonStandard.isLoading
                btnCounterButtonBordered.isLoading = !btnCounterButtonBordered.isLoading
            }
            var dimensionState = false
            btnDimension.setOnClickListener {
                if (dimensionState) {
                    btnCounterButtonStandard.dimension = Dimension.L
                    btnCounterButtonBordered.dimension = Dimension.L
                    dimensionState = false
                } else {
                    btnCounterButtonStandard.dimension = Dimension.XS
                    btnCounterButtonBordered.dimension = Dimension.XS
                    dimensionState = true
                }
            }
        }
    }
}