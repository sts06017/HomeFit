package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.rabbito.homefit.data.User
import kr.rabbito.homefit.data.UserDB
import kr.rabbito.homefit.databinding.ActivityInitBinding

class InitActivity : AppCompatActivity() {
    private var mBinding: ActivityInitBinding? = null
    private val binding get() = mBinding!!

    private var userDB: UserDB? = null
    private var userId: Long? = null

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityInitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getStringExtra("INIT_FROM")

        userDB = UserDB.getInstance(this)

        userId = 0L  // 임시
        loadUserById(userId!!)  // 사용자 정보 불러오고 EditText에 적용

        binding.initBtnMealCountSub.setOnClickListener {
            var cnt = binding.initEtMealCount.text.toString().toInt()
            if (cnt > 0) cnt--
            binding.initEtMealCount.setText(cnt.toString())
        }
        binding.initBtnMealCountAdd.setOnClickListener {
            var cnt = binding.initEtMealCount.text.toString().toInt()
            if (cnt < 9) cnt++
            binding.initEtMealCount.setText(cnt.toString())
        }

        binding.initBtnRegister.setOnClickListener {
            val userName = binding.initEtName.text.toString()
            val height = binding.initEtHeight.text.toString().toInt()
            val weight = binding.initEtWeight.text.toString().toInt()
            val mealCount = binding.initEtMealCount.text.toString().toInt()
            val favWorkout = "턱걸이"  // 임시
            val basicDiet = "기본"    // 임시

            if (user == null) { // 사용자 정보를 불러오지 못한 경우
                val newUser = User(0, userName, height, weight, mealCount, favWorkout, basicDiet)
                insertUser(newUser)
            } else {    // 사용자 정보를 불러온 경우
                updateUserById(userId!!, userName, height, weight, mealCount, favWorkout, basicDiet)
            }

            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun loadUserById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val users = userDB?.userDAO()?.getUserById(id)
            if (users!!.isNotEmpty()) {
                user = users[0]
            }

            if (user != null) {
                initEditText(user!!)
            }
        }
    }

    private fun initEditText(user: User) {
        binding.initEtName.setText(user.userName)
        binding.initEtHeight.setText(user.height.toString())
        binding.initEtWeight.setText(user.weight.toString())
        binding.initEtMealCount.setText(user.mealCount.toString())
    }

    private fun insertUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            userDB?.userDAO()?.insert(user)
        }
    }

    private fun updateUserById(id: Long, userName: String, height: Int, weight: Int, mealCount: Int, favWorkout: String, basicDiet: String) {
        CoroutineScope(Dispatchers.IO).launch {
            userDB?.userDAO()?.updateUserById(id, userName, height, weight, mealCount, favWorkout, basicDiet)
        }
    }
}