package kr.rabbito.homefit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.rabbito.homefit.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private var mBinding: ActivityProfileBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.profileBtnSetList.setOnClickListener {
            startActivity(Intent(this, RoutineListActivity::class.java))
        }
    }
}