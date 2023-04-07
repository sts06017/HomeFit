package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.rabbito.homefit.databinding.ActivityRoutineBinding
import kr.rabbito.homefit.screens.navigatorBar.SettingFragment

class RoutineActivity : AppCompatActivity() {
    private var mBinding: ActivityRoutineBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.routineBtnAddSet.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("VIEW_PAGER_INDEX",3)
            startActivity(intent)
        }
    }
}