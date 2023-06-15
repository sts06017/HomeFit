package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.rabbito.homefit.R
import kr.rabbito.homefit.data.User
import kr.rabbito.homefit.data.UserDB
import kr.rabbito.homefit.databinding.ActivityInitBinding
import android.widget.AdapterView

class InitActivity : AppCompatActivity() {
    private var mBinding: ActivityInitBinding? = null
    private val binding get() = mBinding!!

    private var userDB: UserDB? = null
    private var userId: Long? = null

    private var user: User? = null

    private lateinit var favRoutineSpinner: Spinner
    private var favRoutineSpinnerItems: Array<String>? = null
    private lateinit var favWorkout: String
    private lateinit var spinnerRoutineText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityInitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getStringExtra("INIT_FROM")

        userDB = UserDB.getInstance(this)
        userId = 0L  // 임시
        loadUserById(userId!!)  // 사용자 정보 불러오고 EditText에 적용

        favRoutineSpinner = findViewById<Spinner>(R.id.init_v_fav_workout) // 선호 운동 스피너 설정
        favRoutineSpinnerItems = resources.getStringArray(R.array.routine_btn_album_items)
        favRoutineSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, favRoutineSpinnerItems ?: emptyArray()).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        favRoutineSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = favRoutineSpinnerItems?.get(position)
                // 선택된 항목 처리
                selectedItem?.let {
                    favWorkout = it
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // 선택된 항목이 없을 때 수행할 동작 정의
            }
        }

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
            val basicDiet = "기본"    // 임시

            if (user == null) { // 사용자 정보를 불러오지 못한 경우
                val newUser = User(0, userName, height, weight, mealCount, favWorkout, basicDiet)
                insertUser(newUser)
            } else {    // 사용자 정보를 불러온 경우
                Log.e("favWorkout", favWorkout)
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

        spinnerRoutineText = user.favWorkout.toString() // 스피너 불러온 내용 적용
        for (i in 0 until favRoutineSpinner.adapter.count) {
            val item = favRoutineSpinner.adapter.getItem(i)
            if (item.toString() == spinnerRoutineText) {
                favRoutineSpinner.setSelection(i)
                favWorkout = item.toString()
                break
            }
        }
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