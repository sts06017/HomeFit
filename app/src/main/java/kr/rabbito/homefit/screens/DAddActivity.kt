package kr.rabbito.homefit.screens

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kr.rabbito.homefit.databinding.ActivityDaddBinding

class DAddActivity : AppCompatActivity() {
    private var mBinding: ActivityDaddBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDaddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.daddBtnBack.setOnClickListener{
            finish()
        }
    }
}