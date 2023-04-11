package kr.rabbito.homefit.screens.navigatorBar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.rabbito.homefit.R
import kr.rabbito.homefit.client.FOOD_CLASSES
import kr.rabbito.homefit.client.FOOD_NAMES_KR
import kr.rabbito.homefit.client.calcCalorie
import kr.rabbito.homefit.databinding.FragmentDreportBinding
import kr.rabbito.homefit.screens.DAddActivity
import kr.rabbito.homefit.screens.DHistoryActivity

// 기존의 DReportActivity.kt 파일
class DReportFragment : Fragment() {
    private var mBinding: FragmentDreportBinding? = null
    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val foodName = arguments?.getString("FOOD_NAME")
        val foodQuantity = arguments?.getString("FOOD_QUANTITY")

        val index = FOOD_CLASSES.indexOf(foodName)
        val calorie = calcCalorie(index, foodQuantity!!.toInt())

        binding.dreportTvResultTitle.text = FOOD_NAMES_KR[index]
        binding.dreportTvResultCalorie.text = "${calorie}kcal"
    }

    // activity와 다르게 onCreateView에 코드 작성
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mBinding = FragmentDreportBinding.inflate(inflater, container, false)

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