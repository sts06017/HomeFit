package kr.rabbito.homefit.screens.navigatorBar

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.rabbito.homefit.databinding.FragmentMainBinding
import kr.rabbito.homefit.screens.WODetailActivity


class MainFragment : Fragment() {

    private var mBinding: FragmentMainBinding? = null
    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // activity와 다르게 onCreateView에 코드 작성
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMainBinding.inflate(inflater, container, false)

        binding.mainBtn1.setOnClickListener {
            val intent = Intent(activity, WODetailActivity::class.java)
            intent.putExtra("index", 0)
            activity?.startActivity(intent)
        }
        binding.mainBtn2.setOnClickListener {
            val intent = Intent(activity, WODetailActivity::class.java)
            intent.putExtra("index", 1)
            activity?.startActivity(intent)
        }
        binding.mainBtn3.setOnClickListener {
            val intent = Intent(activity, WODetailActivity::class.java)
            intent.putExtra("index", 2)
            activity?.startActivity(intent)
        }
        binding.mainBtn4.setOnClickListener {
            val intent = Intent(activity, WODetailActivity::class.java)
            intent.putExtra("index", 3)
            activity?.startActivity(intent)
        }
        binding.mainBtn5.setOnClickListener {
            val intent = Intent(activity, WODetailActivity::class.java)
            intent.putExtra("index", 4)
            activity?.startActivity(intent)
        }
        binding.mainBtn6.setOnClickListener {
            val intent = Intent(activity, WODetailActivity::class.java)
            intent.putExtra("index", 5)
            activity?.startActivity(intent)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}