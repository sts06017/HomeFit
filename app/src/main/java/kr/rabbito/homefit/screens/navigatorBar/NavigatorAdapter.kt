package kr.rabbito.homefit.screens.navigatorBar
// 네비게이터 바에 등록된 fragment를 관리하는 파일

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class NavigatorAdapter(fm : FragmentManager, private val fragmentCount : Int) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        Log.d("디버깅","call adapter")

        return when (position) {
            0 -> MainFragment()
            1 -> DReportFragment()
            2 -> ProfileFragment()
            3 -> SettingFragment()
            else -> {Fragment()}
        }
    }
    override fun getCount(): Int = fragmentCount // { return fragmentCount }
}