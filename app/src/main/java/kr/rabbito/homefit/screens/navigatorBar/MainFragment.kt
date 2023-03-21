package kr.rabbito.homefit.screens.navigatorBar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import kr.rabbito.homefit.R
import kr.rabbito.homefit.databinding.FragmentMainBinding
import kr.rabbito.homefit.screens.MainActivity
import kr.rabbito.homefit.screens.WODetailActivity


class MainFragment : Fragment() {

    private var mBinding: FragmentMainBinding? = null
    private val binding get() = mBinding!!
    lateinit var mainActivity: MainActivity
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

        // 운동 이미지 리스트
        val images = arrayOf(
            R.drawable.push_up,
            R.drawable.chin_up,
            R.drawable.squat,
            R.drawable.side_lateral_raise,
            R.drawable.dumbbell_curl,
            R.drawable.leg_raise
        )

        // 그리드뷰 어댑터에 이미지 리스트 연결
        val adapter = ImageAdapter(mainActivity,images)
        binding.mainGvWos.adapter=adapter

        // 각 이미지 클릭시 이벤트리스너 설정
        binding.mainGvWos.setOnItemClickListener { parent, view, position, id ->
            val imageNum = images[position]
            val intent = Intent(activity, WODetailActivity::class.java)

            when (imageNum){
                R.drawable.push_up ->{
                    intent.putExtra("index",0)
                    activity?.startActivity(intent)
                }
                R.drawable.chin_up ->{
                    intent.putExtra("index",1)
                    activity?.startActivity(intent)
                }
                R.drawable.squat ->{
                    intent.putExtra("index",2)
                    activity?.startActivity(intent)
                }
                R.drawable.side_lateral_raise ->{
                    intent.putExtra("index",3)
                    activity?.startActivity(intent)
                }
                R.drawable.dumbbell_curl ->{
                    intent.putExtra("index",4)
                    activity?.startActivity(intent)
                }
                R.drawable.leg_raise ->{
                    intent.putExtra("index",5)
                    activity?.startActivity(intent)
                }
            }
        }
        return binding.root
    }

    // 그리드뷰 어댑터 클래스
    class ImageAdapter(private val context: Context, private val images: Array<Int>) : BaseAdapter() {

        override fun getCount(): Int {
            return images.size
        }

        override fun getItem(position: Int): Any {
            return images[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var imageView = convertView as ImageView?
            if (imageView == null) {
                imageView = ImageView(context)
                imageView.layoutParams = AbsListView.LayoutParams(350, 350)
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            imageView.setImageResource(images[position])
            return imageView
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}