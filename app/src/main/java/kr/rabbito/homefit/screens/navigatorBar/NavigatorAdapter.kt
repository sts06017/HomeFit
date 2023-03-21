package kr.rabbito.homefit.screens.navigatorBar

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class NavigatorAdapter(fm : FragmentManager, private val fragmentCount : Int) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
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