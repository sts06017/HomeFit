package kr.rabbito.homefit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.rabbito.homefit.databinding.ActivityDaddBinding

class DAddActivity : AppCompatActivity() {
    private var mBinding: ActivityDaddBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDaddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.daddBtnAddDiet.setOnClickListener {
            startActivity(Intent(this, DCameraActivity::class.java))
        }
    }
}