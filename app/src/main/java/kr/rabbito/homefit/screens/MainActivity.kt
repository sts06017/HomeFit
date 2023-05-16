package kr.rabbito.homefit.screens
// 4개의 fragment 를 담고있는 Activity
// MainActivity 위에 (mainFragment, dReportFragment, profileFragment, settingFragment)가 존재함.
// 해당 activity에서는 네비게이션바와 fragment를 연결하는 코드만 작성.
// 각 fragment에서 context가 요구될경우에는 requireContext() 메소드를 사용하면 MainActivity의 context가 전달됨.

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

    private val binding get() = mbinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pageNum = intent.getIntExtra("VIEW_PAGER_INDEX",0)
        configureBottomNavigation(pageNum)
    }

    private fun configureBottomNavigation(pageNum : Int = 0){

        val result = intent.getStringExtra("DATA")

        // 네비게이터 adapter 연결
        binding.mainVpViewpager.adapter = NavigatorAdapter(supportFragmentManager, 4, result)   // fragmentCount 인자는 넣을 페이지 수

        binding.mainTlMenubar.setupWithViewPager(binding.mainVpViewpager)

        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.navigation_bar, null, false)

        // main activity에 하단 네비게이터 버튼 연결
        binding.mainTlMenubar.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.nvBar_btn_workout) as RelativeLayout
        binding.mainTlMenubar.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.nvBar_btn_diet) as RelativeLayout
        binding.mainTlMenubar.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.nvBar_btn_profile) as RelativeLayout
        binding.mainTlMenubar.getTabAt(3)!!.customView = bottomNaviLayout.findViewById(R.id.nvBar_btn_setting) as RelativeLayout
        binding.mainVpViewpager.currentItem = pageNum
    }
}