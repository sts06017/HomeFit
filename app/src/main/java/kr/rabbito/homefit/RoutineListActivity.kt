package kr.rabbito.homefit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.rabbito.homefit.databinding.ActivityRoutinelistBinding

class RoutineListActivity : AppCompatActivity() {
    private var mBinding: ActivityRoutinelistBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRoutinelistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dreportBtnAdd.setOnClickListener {
            startActivity(Intent(this, RoutineActivity::class.java))
        }
    }
}