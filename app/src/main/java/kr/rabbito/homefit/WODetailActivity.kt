package kr.rabbito.homefit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.rabbito.homefit.databinding.ActivityWodetailBinding

class WODetailActivity : AppCompatActivity() {
    private var mBinding: ActivityWodetailBinding? = null
    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWodetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.wodetailBtnStartWo.setOnClickListener {
            startActivity(Intent(this, WOActivity::class.java))
        }
    }
}