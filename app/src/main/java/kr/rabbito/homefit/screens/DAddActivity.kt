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

    private lateinit var getContent: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDaddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val intent = Intent(this, DImageActivity::class.java)
                intent.putExtra("SELECTED_IMAGE", uri.toString())

                startActivity(intent)
            }
        }

        binding.daddBtnCamera.setOnClickListener {
            startActivity(Intent(this, DCameraActivity::class.java))
        }

        binding.daddBtnAlbum.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.daddBtnBack.setOnClickListener{
            finish()
        }
    }
}