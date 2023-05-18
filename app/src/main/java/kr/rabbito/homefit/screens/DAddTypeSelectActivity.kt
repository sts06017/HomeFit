package kr.rabbito.homefit.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.rabbito.homefit.databinding.ActivityDaddTypeSelectBinding

class DAddTypeSelectActivity : AppCompatActivity() {
    private var mBinding: ActivityDaddTypeSelectBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDaddTypeSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}