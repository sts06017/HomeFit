package kr.rabbito.homefit.screens.navigatorBar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.rabbito.homefit.R
import kr.rabbito.homefit.databinding.FragmentDreportBinding
import kr.rabbito.homefit.screens.DAddActivity
import kr.rabbito.homefit.screens.DHistoryActivity

// 기존의 DReportActivity.kt 파일
class DReportFragment : Fragment() {
    private var mBinding: FragmentDreportBinding? = null
    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("디버깅","d onCreate")

        super.onCreate(savedInstanceState)
    }

    // activity와 다르게 onCreateView에 코드 작성
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("디버깅","d onCreateView1")

        mBinding = FragmentDreportBinding.inflate(inflater, container, false)
        Log.d("디버깅","d onCreateView2")

        binding.dreportBtnAdd.setOnClickListener {
            startActivity(Intent(activity, DAddActivity::class.java))
        }

        binding.dreportBtnHistory.setOnClickListener {
            startActivity(Intent(activity, DHistoryActivity::class.java))
        }
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}