package kr.rabbito.homefit.screens.navigatorBar

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.rabbito.homefit.data.User
import kr.rabbito.homefit.data.UserDB
import kr.rabbito.homefit.databinding.FragmentProfileBinding
import kr.rabbito.homefit.screens.MainActivity
import kr.rabbito.homefit.screens.RoutineListActivity


class ProfileFragment : Fragment() {
    private var mBinding: FragmentProfileBinding? = null
    private val binding get() = mBinding!!
    private val mainActivity = context as MainActivity

    private var userDB: UserDB? = null
    private var userId: Long? = null

    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    // activity와 다르게 onCreateView에 코드 작성
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentProfileBinding.inflate(inflater, container, true)

        userDB = UserDB.getInstance(mainActivity)

        userId = 0L  // 임시
        loadUserById(userId!!)  // 사용자 정보 불러오고 TextView에 적용

        binding.profileBtnSetList.setOnClickListener {
            startActivity(Intent(activity, RoutineListActivity::class.java))
        }
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
    private fun loadUserById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val users = userDB?.userDAO()?.getUserById(id)
            if (users!!.isNotEmpty()) {
                user = users[0]
            }

            if (user != null) {
                initTextView(user!!)
            }
        }
    }
    private fun initTextView(user: User) {
        binding.profileTvUsername.text = user.userName
        binding.profileTvHeight.text = user.height.toString()
        binding.profileTvWeight.text = user.weight.toString()
        binding.profileTvMealCount.text = user.mealCount.toString()
        binding.profileTvFavWorkout.text = user.favWorkout
        binding.profileTvBasicDiet.text = user.basicDiet
    }
}