package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kr.rabbito.homefit.databinding.ActivityWodetailBinding
import kr.rabbito.homefit.workout.WorkoutData
import kr.rabbito.homefit.workout.WorkoutState

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
        WorkoutState.setTotal = 3

        binding.wodetailBtnStartWo.setOnClickListener {
            // 임시
            val intent = Intent(this, WOActivity::class.java)
            intent.putExtra("index", index)
            startActivity(intent)
        }
        binding.wodetailBtnLoad.setOnClickListener {
            val intent = Intent(this, RoutineListActivity::class.java)
            startActivity(intent)
        }
        binding.wodetailBtnSetCountSub.setOnClickListener{
            if(WorkoutState.setTotal > 0) {
                WorkoutState.setTotal -= 1
            }
            else{
                Toast.makeText(this, "더 낮출 수 없습니다",Toast.LENGTH_SHORT).show()
            }
            binding.wodetailEtSetCount.hint = WorkoutState.setTotal.toString()
        }
        binding.wodetailBtnSetCountAdd.setOnClickListener {
            WorkoutState.setTotal += 1
            binding.wodetailEtSetCount.hint = WorkoutState.setTotal.toString()
        }
        binding.wodetailBtnRestCountSub.setOnClickListener {

        }
        binding.wodetailBtnRestCountAdd.setOnClickListener {

        }
    }
}