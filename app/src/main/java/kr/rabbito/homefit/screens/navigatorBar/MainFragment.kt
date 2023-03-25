package kr.rabbito.homefit.screens.navigatorBar

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kr.rabbito.homefit.R
import kr.rabbito.homefit.databinding.FragmentMainBinding
import kr.rabbito.homefit.screens.MainActivity
import kr.rabbito.homefit.screens.WoImageAdapter


class MainFragment : Fragment() {

    private var mBinding: FragmentMainBinding? = null
    private val binding get() = mBinding!!
    lateinit var mainActivity: MainActivity
    var waistCalled = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    // activity와 다르게 onCreateView에 코드 작성
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMainBinding.inflate(inflater, container, false)

        // 운동 태그 리스트
        var tagBtns = arrayOf(binding.mainBtnAll, binding.mainBtnChest, binding.mainBtnShoulder, binding.mainBtnArm, binding.mainBtnWaist)
        val tagBtnBoolean = booleanArrayOf(false, false, false, false, false)
//        for(i in tagBtns.indices){
//            tagBtns[i].setOnClickListener {
//                pressButton(i, tagBtns, tagBtnBoolean)
//            }
//        }

        // 운동 이미지 리스트
        val woImages = arrayOf(
            R.drawable.push_up,
            R.drawable.chin_up,
            R.drawable.squat,
            R.drawable.side_lateral_raise,
            R.drawable.dumbbell_curl,
            R.drawable.leg_raise
        )

        // 그리드뷰 어댑터에 이미지 리스트 연결
//        val woAdapter = WoImageAdapter(mainActivity,woImages)
//        binding.mainGvWos.adapter=woAdapter

        // 그리드뷰 참조하기
        val woGridView = binding.mainGvWos

        // custom adapter 객체 생성
        val adapter = WoImageAdapter(mainActivity,woImages)

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


        // 각 이미지 클릭시 이벤트리스너 설정
//        binding.mainGvWos.setOnItemClickListener { parent, view, position, id ->
//            val imageNum = woImages[position]
//            val intent = Intent(activity, WODetailActivity::class.java)
//
//4
//            when (imageNum){
//                R.drawable.push_up ->{
//                    Log.d("gv test","$imageNum,")
//                    intent.putExtra("index",0)
//                    activity?.startActivity(intent)
//                }
//                R.drawable.chin_up ->{
//                    Log.d("gv test","$imageNum,")
//                    intent.putExtra("index",1)
//                    activity?.startActivity(intent)
//                }
//                R.drawable.squat ->{
//                    Log.d("gv test","$imageNum,")
//                    intent.putExtra("index",2)
//                    activity?.startActivity(intent)
//                }
//                R.drawable.side_lateral_raise ->{
//                    Log.d("gv test","$imageNum,")
//                    intent.putExtra("index",3)
//                    activity?.startActivity(intent)
//                }
//                R.drawable.dumbbell_curl ->{
//                    Log.d("gv test","$imageNum,")
//                    intent.putExtra("index",4)
//                    activity?.startActivity(intent)
//                }
//                R.drawable.leg_raise ->{
//                    Log.d("gv test","$imageNum,")
//                    intent.putExtra("index",5)
//                    activity?.startActivity(intent)
//                }
//            }
//        }
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
        super.onDestroyView()
        mBinding = null
    }
}