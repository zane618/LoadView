package com.example.loadview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import com.example.loadview.view.ProgressBarView
import com.example.loadview.view.RoundProgressBar
import java.util.*

const val MSG_UPDATE = 0x110

class MainActivity : AppCompatActivity() {

    lateinit var progressBarView1: ProgressBarView
    lateinit var p2: com.example.loadview.ProgressBarView

    lateinit var roundMine: RoundProgressBar
    lateinit var roundOther: com.example.loadview.RoundProgressBar

    val timer: Timer = Timer()
    val task: TimerTask = object : TimerTask(){
        override fun run() {
            var progress = progressBarView1.progress
            progressBarView1.setProgress(++progress)

            p2.setProgress(progress)

            roundMine.setProgress(progress)
            roundOther.setProgress(progress)


            if (progress >= 100) {
                timer.cancel()
            }
            Log.e("xxxxx", progress.toString())
        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBarView1 = findViewById(R.id.progress_view_1)
        p2 = findViewById(R.id.progress_view_2)

        roundMine = findViewById(R.id.round_mine)
        roundOther = findViewById(R.id.round_other)

        timer.schedule(task, 1000, 100)

    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}
