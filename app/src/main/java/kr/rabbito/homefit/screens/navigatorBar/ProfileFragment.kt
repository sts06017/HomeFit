package kr.rabbito.homefit.screens.navigatorBar

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.rabbito.homefit.databinding.FragmentProfileBinding
import kr.rabbito.homefit.screens.RoutineListActivity


class ProfileFragment : Fragment() {
    private var mBinding: FragmentProfileBinding? = null
    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    // activity와 다르게 onCreateView에 코드 작성
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.profileBtnSetList.setOnClickListener {
            startActivity(Intent(activity, RoutineListActivity::class.java))
        }
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}