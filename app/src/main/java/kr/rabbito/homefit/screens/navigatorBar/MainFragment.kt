package kr.rabbito.homefit.screens.navigatorBar

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kr.rabbito.homefit.R
import kr.rabbito.homefit.databinding.FragmentMainBinding
import kr.rabbito.homefit.screens.MainActivity
import kr.rabbito.homefit.screens.WoImageAdapter

// 기존의 MainActivity.kt 파일
class MainFragment : Fragment() {

    private var mBinding: FragmentMainBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    // activity와 다르게 onCreateView에 코드 작성
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("디버깅","m onCreateView")
        mBinding = FragmentMainBinding.inflate(inflater, container, false)

        // 운동 태그 리스트
        var tagBtns = arrayOf(binding.mainBtnAll, binding.mainBtnChest, binding.mainBtnShoulder, binding.mainBtnArm, binding.mainBtnWaist)
        val tagBtnBoolean = booleanArrayOf(false, false, false, false, false)

        // 운동 이미지 리스트
        val woImages = arrayOf(
            R.drawable.push_up,
            R.drawable.chin_up,
            R.drawable.squat,
            R.drawable.side_lateral_raise,
            R.drawable.dumbbell_curl,
            R.drawable.leg_raise
        )

        // 그리드뷰 참조하기
        val woGridView = binding.mainGvWos

        // custom adapter 객체 생성
        val adapter = WoImageAdapter(requireContext(),woImages)

        // gridView에 adapter 설정
        woGridView.adapter = adapter

        binding.mainBtnAll.setOnClickListener {
            pressButton(0, tagBtns, tagBtnBoolean)
            adapter.removeItem()
            adapter.showItem(woImages)
            adapter.notifyDataSetInvalidated()
        }
        binding.mainBtnChest.setOnClickListener {
            pressButton(1, tagBtns, tagBtnBoolean)
            val addImage = arrayOf(woImages.elementAt(0),woImages.elementAt(1))
            adapter.removeItem()
            adapter.showItem(addImage)
            adapter.notifyDataSetInvalidated()
        }
        binding.mainBtnShoulder.setOnClickListener {
            pressButton(2, tagBtns, tagBtnBoolean)
            val addImage = arrayOf(woImages.elementAt(1),woImages.elementAt(3))
            adapter.removeItem()
            adapter.showItem(addImage)
            adapter.notifyDataSetInvalidated()
        }
        binding.mainBtnArm.setOnClickListener {
            pressButton(3, tagBtns, tagBtnBoolean)
            val addImage = arrayOf(woImages.elementAt(0),woImages.elementAt(1),woImages.elementAt(4))
            adapter.removeItem()
            adapter.showItem(addImage)
            adapter.notifyDataSetInvalidated()
        }
        binding.mainBtnWaist.setOnClickListener {
            pressButton(4, tagBtns, tagBtnBoolean)
            val addImage = arrayOf(woImages.elementAt(2))
            adapter.removeItem()
            adapter.showItem(addImage)
            adapter.notifyDataSetInvalidated()
        }

//        return binding.root
        return binding.root
    }
    // 운동 태그 클릭 함수
    private fun pressButton(buttonIndex: Int, btnArray: Array<Button>, btnBoolean: BooleanArray){
        for(i in btnBoolean.indices){
            btnBoolean[i] = (i == buttonIndex)
        }
        for(i in btnBoolean.indices) {
            if(btnBoolean[i]) {
                btnArray[i].setBackgroundResource(R.drawable.main_iv_tag_s_clicked)
                btnArray[i].setTextColor(Color.BLACK)
            }
            else{
                btnArray[i].setBackgroundResource(R.drawable.main_iv_tag_s)
                btnArray[i].setTextColor(Color.WHITE)
            }
        }
    }

    override fun onDestroyView() {
        Log.d("디버깅","m onDestroyView1")
        super.onDestroyView()
        mBinding = null
        Log.d("디버깅","m onDestroyView2")

    }
}