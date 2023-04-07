package kr.rabbito.homefit.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.rabbito.homefit.R
import kr.rabbito.homefit.databinding.ActivityMainBinding
import kr.rabbito.homefit.screens.navigatorBar.*

class MainActivity : AppCompatActivity() {

    private lateinit var mbinding: ActivityMainBinding
    private val binding get() = mbinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("디버깅","activity onCreate")

        val pageNum = intent.getIntExtra("VIEW_PAGER_INDEX",0)
        configureBottomNavigation(pageNum)

    }
    private fun configureBottomNavigation(pageNum : Int = 0){
        Log.d("디버깅","activity configure")

        // 네비게이터 adapter 연결
        binding.mainVpViewpager.adapter = NavigatorAdapter(supportFragmentManager, 4)   // fragmentCount 인자는 넣을 페이지 수
        Log.d("디버깅","activity configure1")

        binding.mainTlMenubar.setupWithViewPager(binding.mainVpViewpager)
        Log.d("디버깅","activity configure2")

        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.navigation_bar, null, false)
        Log.d("디버깅","activity configure3")

        // main activity에 하단 네비게이터 버튼 연결
        binding.mainTlMenubar.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.nvBar_btn_workout) as RelativeLayout
        binding.mainTlMenubar.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.nvBar_btn_diet) as RelativeLayout
        binding.mainTlMenubar.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.nvBar_btn_profile) as RelativeLayout
        binding.mainTlMenubar.getTabAt(3)!!.customView = bottomNaviLayout.findViewById(R.id.nvBar_btn_setting) as RelativeLayout
        binding.mainVpViewpager.currentItem = pageNum
    }
}