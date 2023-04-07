package kr.rabbito.homefit.screens.navigatorBar

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