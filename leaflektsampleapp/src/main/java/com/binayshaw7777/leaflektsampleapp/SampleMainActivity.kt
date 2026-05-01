package com.binayshaw7777.leaflektsampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.binayshaw7777.leaflektsampleapp.ui.theme.LeafleKTTheme

class SampleMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LeafleKTTheme {
                SampleAppScreen()
            }
        }
    }
}
