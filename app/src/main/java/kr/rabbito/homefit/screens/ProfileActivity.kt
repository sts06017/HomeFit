package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.rabbito.homefit.data.User
import kr.rabbito.homefit.data.UserDB
import kr.rabbito.homefit.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private var mBinding: ActivityProfileBinding? = null
    private val binding get() = mBinding!!

    private var userDB: UserDB? = null
    private var userId: Long? = null

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = UserDB.getInstance(this)

        userId = 0L  // 임시
        loadUserById(userId!!)  // 사용자 정보 불러오고 TextView에 적용

        binding.profileBtnSetList.setOnClickListener {
            startActivity(Intent(this, RoutineListActivity::class.java))
        }
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