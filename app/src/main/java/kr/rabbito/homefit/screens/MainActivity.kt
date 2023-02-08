package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.rabbito.homefit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 임시
        binding.mainClTop.setOnClickListener {
//            startActivity(Intent(this, WODetailActivity::class.java))
        }


        // 임시
        binding.mainBtn1.setOnClickListener {
            val intent = Intent(this, WODetailActivity::class.java)
            intent.putExtra("index", 0)
            startActivity(intent)
        }
        binding.mainBtn2.setOnClickListener {
            val intent = Intent(this, WODetailActivity::class.java)
            intent.putExtra("index", 1)
            startActivity(intent)
        }
        binding.mainBtn3.setOnClickListener {
            val intent = Intent(this, WODetailActivity::class.java)
            intent.putExtra("index", 2)
            startActivity(intent)
        }
        binding.mainBtn4.setOnClickListener {
            val intent = Intent(this, WODetailActivity::class.java)
            intent.putExtra("index", 3)
            startActivity(intent)
        }
        binding.mainBtn5.setOnClickListener {
            val intent = Intent(this, WODetailActivity::class.java)
            intent.putExtra("index", 4)
            startActivity(intent)
        }
        binding.mainBtn6.setOnClickListener {
            val intent = Intent(this, WODetailActivity::class.java)
            intent.putExtra("index", 5)
            startActivity(intent)
        }

    }
}