package kr.rabbito.homefit.screens

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kr.rabbito.homefit.databinding.ActivityDaddTypeSelectBinding

class DAddTypeSelectActivity : AppCompatActivity() {
    private var mBinding: ActivityDaddTypeSelectBinding? = null
    private val binding get() = mBinding!!

    private lateinit var getContent: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDaddTypeSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val intent = Intent(this, DImageActivity::class.java)
                intent.putExtra("SELECTED_IMAGE", uri.toString())

                startActivity(intent)
            }
        }

        binding.daddTypeSelectBtnCamera.setOnClickListener {
            startActivity(Intent(this, DCameraActivity::class.java))
        }

        binding.daddTypeSelectBtnAlbum.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.daddTypeSelectBtnPassiveInput.setOnClickListener {
            val intent = Intent(this, DAddActivity::class.java)
            startActivity(intent)
        }

        binding.daddTypeSelectBtnBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("VIEW_PAGER_INDEX", 1)
            startActivity(intent)
        }
    }
}