package kr.rabbito.homefit.screens

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kr.rabbito.homefit.R
import kr.rabbito.homefit.databinding.ActivityMainBinding
import kr.rabbito.homefit.screens.navigatorBar.NavigatorAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var mbinding: ActivityMainBinding
    private val binding get() = mbinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureBottomNavigation()
    }
    private fun configureBottomNavigation(){
        // 네비게이터 adapter 연결
        binding.mainVpViewpager.adapter = NavigatorAdapter(supportFragmentManager, 4)   // fragmentCount 인자는 넣을 페이지 수

        binding.mainTlMenubar.setupWithViewPager(binding.mainVpViewpager)

        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.navigation_bar, null, false)

        // main activity에 하단 네비게이터 버튼 연결
        binding.mainTlMenubar.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.nvBar_btn_workout) as RelativeLayout
        binding.mainTlMenubar.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.nvBar_btn_diet) as RelativeLayout
        binding.mainTlMenubar.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.nvBar_btn_profile) as RelativeLayout
        binding.mainTlMenubar.getTabAt(3)!!.customView = bottomNaviLayout.findViewById(R.id.nvBar_btn_setting) as RelativeLayout
    }
}