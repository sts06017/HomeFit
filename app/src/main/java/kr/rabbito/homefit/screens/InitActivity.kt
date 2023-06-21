package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
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
import android.widget.Toast
import androidx.core.view.marginStart
import kr.rabbito.homefit.utils.calc.Converter
import kotlin.math.roundToInt

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

    private lateinit var basicDietSpinner: Spinner
    private var basicDietSpinnerItems: Array<String>? = null
    private lateinit var basicDiet: String
    private lateinit var spinnerBasicDietText: String

    private lateinit var screenType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityInitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        screenType = intent.getStringExtra("INIT_FROM").toString()
        Log.e("INIT_FROM", screenType)
        initChangeText(screenType)

        userDB = UserDB.getInstance(this)
        userId = 0L  // 임시
        loadUserById(userId!!)  // 사용자 정보 불러오고 EditText에 적용

        favRoutineSpinner =
            findViewById<Spinner>(R.id.init_v_fav_workout) as Spinner // 선호 운동 스피너 설정
        favRoutineSpinnerItems = resources.getStringArray(R.array.routine_btn_album_items)
        favRoutineSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            favRoutineSpinnerItems ?: emptyArray()
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        basicDietSpinner = findViewById<Spinner>(R.id.init_v_basic_diet) as Spinner // 기본 식습관 스피너 설정
        basicDietSpinnerItems = resources.getStringArray(R.array.basic_diet_items)
        basicDietSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            basicDietSpinnerItems ?: emptyArray()
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        favRoutineSpinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener { // 선호 운동 선택
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
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

        basicDietSpinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener { // 기본 식습관 선택
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = basicDietSpinnerItems?.get(position)
                    // 선택된 항목 처리
                    selectedItem?.let {
                        basicDiet = it
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // 선택된 항목이 없을 때 수행할 동작 정의
                }
            }

        binding.initBtnBack.setOnClickListener {
            finish()
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
            val heightText = binding.initEtHeight.text.toString()
            val weightText = binding.initEtWeight.text.toString()
            val mealCountText = binding.initEtMealCount.text.toString()

            if (isFieldsEmpty()) {
                return@setOnClickListener
            } else {
                val height = heightText.toInt()
                val weight = weightText.toInt()
                val mealCount = mealCountText.toInt()

                if (user == null) { // 사용자 정보를 불러오지 못한 경우
                    val newUser =
                        User(0, userName, height, weight, mealCount, favWorkout, basicDiet)
                    insertUser(newUser)
                    startActivity(Intent(this, MainActivity::class.java))
                } else {    // 사용자 정보를 불러온 경우
                    Log.e("favWorkout", favWorkout)
                    updateUserById(userId!!, userName, height, weight, mealCount, favWorkout, basicDiet)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("VIEW_PAGER_INDEX", 2)
                    startActivity(intent)
                }
            }
        }
    }

    private fun isFieldsEmpty(): Boolean {
        val userName = binding.initEtName.text.toString()
        val heightText = binding.initEtHeight.text.toString()
        val weightText = binding.initEtWeight.text.toString()
        val mealCountText = binding.initEtMealCount.text.toString()

        if (userName.isNullOrEmpty() || heightText.isNullOrEmpty() || weightText.isNullOrEmpty() || mealCountText.isNullOrEmpty()) {
            Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return true
        }else if(heightText == "0" || weightText == "0" || mealCountText == "0"){
            Toast.makeText(this, "0 이상의 숫자를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    private fun isAllTextNotEmpty(vararg texts: String): Boolean {
        for (text in texts) {
            if (text.isNullOrEmpty()) {
                Toast.makeText(this, "모든 사용자 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun initChangeText(screenType: String) {
        if (screenType == "ProfileFragment") {
            binding.initTvTitle.text = "프로필 수정"
            val params = binding.initTvTitle.layoutParams as ViewGroup.MarginLayoutParams
            params.marginStart = Converter.dpToPx(resources, 36)
            binding.initTvTitle.layoutParams = params

            binding.initBtnBack.visibility = View.VISIBLE

            binding.initBtnRegister.text = "정보 수정"
        } else {
            Log.d("check init", "check")
            binding.initTvTitle.text = "환영합니다."
            val params = binding.initTvTitle.layoutParams as ViewGroup.MarginLayoutParams
            params.marginStart = Converter.dpToPx(resources, 20)
            binding.initTvTitle.layoutParams = params

            binding.initBtnBack.visibility = View.GONE

            binding.initBtnRegister.text = "사용자 등록"
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

        spinnerBasicDietText = user.basicDiet.toString()
        for (i in 0 until basicDietSpinner.adapter.count) {
            val item = basicDietSpinner.adapter.getItem(i)
            if (item.toString() == spinnerBasicDietText) {
                basicDietSpinner.setSelection(i)
                basicDiet = item.toString()
                break
            }
        }
    }

    private fun insertUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            userDB?.userDAO()?.insert(user)
        }
    }

    private fun updateUserById(
        id: Long,
        userName: String,
        height: Int,
        weight: Int,
        mealCount: Int,
        favWorkout: String,
        basicDiet: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            userDB?.userDAO()
                ?.updateUserById(id, userName, height, weight, mealCount, favWorkout, basicDiet)
        }
    }
}