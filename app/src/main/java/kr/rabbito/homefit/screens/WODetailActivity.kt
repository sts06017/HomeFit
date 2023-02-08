package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kr.rabbito.homefit.databinding.ActivityWodetailBinding
import kr.rabbito.homefit.workout.WorkoutData

class WODetailActivity : AppCompatActivity() {
    private var mBinding: ActivityWodetailBinding? = null
    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWodetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 임시
        val index = intent.getIntExtra("index", 0)

        binding.wodetailTvTitle.text = WorkoutData.workoutNamesKOR[index]
        binding.wodetailTvSubTitle.text = WorkoutData.workoutNamesENG[index]

        binding.wodetailBtnStartWo.setOnClickListener {
            // 임시
            val intent = Intent(this, WOActivity::class.java)
            intent.putExtra("index", index)
            startActivity(intent)
        }
    }
}