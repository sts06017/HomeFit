package kr.rabbito.homefit.screens.navigatorBar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.rabbito.homefit.R
import kr.rabbito.homefit.databinding.FragmentProfileBinding
import kr.rabbito.homefit.databinding.FragmentSettingBinding

// 기존의 SettingFragment.kt 파일
class SettingFragment : Fragment() {
    private var mBinding: FragmentSettingBinding? = null
    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}