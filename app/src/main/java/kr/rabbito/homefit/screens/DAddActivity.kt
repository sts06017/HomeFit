package kr.rabbito.homefit.screens

import android.content.Intent
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.rabbito.homefit.R
import kr.rabbito.homefit.client.FOOD_CLASSES
import kr.rabbito.homefit.client.FOOD_NAMES_KR
import kr.rabbito.homefit.data.Diet
import kr.rabbito.homefit.data.DietDB
import kr.rabbito.homefit.databinding.ActivityDaddBinding
import kr.rabbito.homefit.screens.calendar.makeInVisible
import kr.rabbito.homefit.screens.calendar.makeVisible
import kr.rabbito.homefit.utils.calc.Converter.Companion.timeFormatter
import kr.rabbito.homefit.utils.setWidthPercentage
import java.time.LocalDate
import java.time.LocalDateTime

data class NewDiet(
    val id: Int?,
    val foodName: String,
    val weight: Double,
    val calorie: Double,
    val carbohydrate: Double,
    val protein: Double,
    val fat: Double,
    val dDate: LocalDate,
    val dTime: String
)

class DAddActivity : AppCompatActivity() {
    private var mBinding: ActivityDaddBinding? = null
    private val binding get() = mBinding!!

    private lateinit var dietItems: Array<String>
    private lateinit var diet: Diet
    private var dietDB: DietDB? = null
    private lateinit var foodName: String
    private var weight = 0
    private var calorie = 0
    private var carbohydrate = 0
    private var protein = 0
    private var fat = 0

    private var fromHistory = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDaddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dietDB = DietDB.getInstance(this)

        editTextListener()  // editTextView 설정
        initSpinner()   // 스피너 초기화 및 생성
        if (intent.hasExtra("DIET")) {
            // db객체를 전달받아 화면을 띄우는 경우 실행됨
            diet = intent.getParcelableExtra("DIET")!!
            Log.d("최승호", "dietName: ${diet.foodName}")
            addToChange()   // 식단정보 변경 페이지 설정
        } else {
            // initView()
            addDietButton()   // 식단정보 추가 페이지 설정
        }

        fromHistory = intent.getBooleanExtra("fromHistory", false)

        binding.daddBtnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initView() {
        // 음식 드롭다운 선택
        // 선택한 음식 무게 입력
        // 입력한 무게의 음식에 맞게 상세정보 띄워주기
        // 식단정보 토대로 Diet db인스턴스 생성후 Insert 작업 수행
    }

    private fun addDietButton() {
        binding.daddBtnAddDiet.setOnClickListener {
            if (isEditTextEmpty()) {
                // Show an error message or perform appropriate action when any of the fields are empty
                return@setOnClickListener
            } else {
                val newDiet = NewDiet(
                    null,
                    FOOD_CLASSES[FOOD_NAMES_KR.indexOf(foodName)],
                    weight.toDouble(),
                    calorie.toDouble(),
                    carbohydrate.toDouble(),
                    protein.toDouble(),
                    fat.toDouble(),
                    LocalDate.now(),
                    LocalDateTime.now().format(timeFormatter)
                )
                val gson = Gson()
                val jsonString = gson.toJson(newDiet)
                val jsonHash = jsonString.hashCode()
                val diet = Diet(
                    id = null,
                    foodName = newDiet.foodName,
                    weight = newDiet.weight,
                    calorie = newDiet.calorie,
                    carbohydrate = newDiet.carbohydrate,
                    protein = newDiet.protein,
                    fat = newDiet.fat,
                    dDate = newDiet.dDate,
                    dTime = newDiet.dTime,
                    jsonHash
                )
                CoroutineScope(Dispatchers.IO).launch {
                    val existingDiet = dietDB!!.DietDAO().findByJsonHash(jsonHash)
                    if (existingDiet == null) {
                        dietDB?.DietDAO()?.insert(diet)
                    }
                }
                Log.d("최승호", "insert Diet ${diet.foodName}, ${diet.weight}")
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("VIEW_PAGER_INDEX", 1)
                intent.putExtra("DATE", diet.dDate.toString())
                startActivity(intent)
                finish()
            }
        }
    }

    private fun addToChange() {
        binding.daddBtnAddDiet.makeInVisible()
        binding.daddBtnChangeDiet.makeVisible()
        binding.daddBtnDeleteDiet.makeVisible()

        binding.daddTvTitle.text = "식단정보 변경"
//        binding.daddBtnAddDiet.text = "식단정보 변경"

        selectecSpinner(diet.foodName!!)
        binding.daddEtFixWeight.setText(diet.weight!!.toInt().toString())
        binding.daddEtFixCalorie.setText(diet.calorie!!.toInt().toString())
        binding.daddEtFixCarbohydrate.setText(diet.carbohydrate!!.toInt().toString())
        binding.daddEtFixProtein.setText(diet.protein!!.toInt().toString())
        binding.daddEtFixFat.setText(diet.fat!!.toInt().toString())

        binding.daddBtnChangeDiet.setOnClickListener {
            // db업데이트 기능 수행
            if (isEditTextEmpty()) {
                // Show an error message or perform appropriate action when any of the fields are empty
                return@setOnClickListener
            } else {
//                Log.d("최승호","update fd:$foodName, w:$weight, cal:$calorie, car:$carbohydrate, pro:$protein, fat:$fat")
                CoroutineScope(Dispatchers.IO).launch { updateDiet() }

                if (fromHistory) {
                    val intent = Intent(this, DReportActivity::class.java)
                    intent.putExtra("DATE", diet.dDate.toString())
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("VIEW_PAGER_INDEX", 1)
                    intent.putExtra("DATE", diet.dDate.toString())
                    startActivity(intent)
                    finish()
                }
            }
        }
        binding.daddBtnDeleteDiet.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.DeleteAlertDialog)
            builder.setMessage("정보를 삭제하시겠습니까?")
                .setPositiveButton("삭제") { dialog, _ ->
                    // db삭제기능 수행
                    CoroutineScope(Dispatchers.IO).launch { deleteDiet() }

                    if (fromHistory) {
                        val intent = Intent(this, DReportActivity::class.java)
                        intent.putExtra("DATE", diet.dDate.toString())
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("VIEW_PAGER_INDEX", 1)
                        intent.putExtra("DATE", diet.dDate.toString())
                        startActivity(intent)
                        finish()
                    }

                }.create().apply {
                    show()
                    setWidthPercentage(0.8)
                }
        }
    }

    private fun isEditTextEmpty(): Boolean {
        val weight = binding.daddEtFixWeight.text.toString().trim()
        val calorie = binding.daddEtFixCalorie.text.toString().trim()
        val carbohydrate = binding.daddEtFixCarbohydrate.text.toString().trim()
        val protein = binding.daddEtFixProtein.text.toString().trim()
        val fat = binding.daddEtFixFat.text.toString().trim()
        if (weight.isNullOrEmpty() || calorie.isNullOrEmpty() || carbohydrate.isNullOrEmpty() || protein.isNullOrEmpty() || fat.isNullOrEmpty()) {
            Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return true
        } else if (weight == "0" || calorie == "0" || carbohydrate == "0" || protein == "0" || fat == "0") {
            Toast.makeText(this, "0 이상의 숫자를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    private fun initSpinner() {
        dietItems = resources.getStringArray(R.array.diet_items)
        binding.daddVDropdown.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, dietItems).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        binding.daddVDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = dietItems?.get(position)
                // 선택된 항목 처리
                selectedItem?.let {
                    foodName = it
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 선택된 항목이 없을 때 수행할 동작 정의
            }
        }
    }

    private fun selectecSpinner(dietName: String) {
        var selectItemPosition = 0

        when (dietName) {
            "Jajangmyeon" -> selectItemPosition = 0
            "Salad" -> selectItemPosition = 1
            "Kimchi Fried Rice" -> selectItemPosition = 2
            "Stir-Fried Pork" -> selectItemPosition = 3
            "Sundaegukbap" -> selectItemPosition = 4
        }
        binding.daddVDropdown.setSelection(selectItemPosition)
    }

    private fun editTextListener() {
        binding.daddEtFixWeight.doAfterTextChanged {
            try {
                weight = binding.daddEtFixWeight.text.toString().toInt()
            } catch (e: java.lang.NumberFormatException) {
                Log.d("ed test", "ed can't formatting")
            }
        }
        binding.daddEtFixCalorie.doAfterTextChanged {
            try {
                calorie = binding.daddEtFixCalorie.text.toString().toInt()
            } catch (e: java.lang.NumberFormatException) {
                Log.d("ed test", "ed can't formatting")
            }
        }
        binding.daddEtFixCarbohydrate.doAfterTextChanged {
            try {
                carbohydrate = binding.daddEtFixCarbohydrate.text.toString().toInt()
            } catch (e: java.lang.NumberFormatException) {
                Log.d("ed test", "ed can't formatting")
            }
        }
        binding.daddEtFixProtein.doAfterTextChanged {
            try {
                protein = binding.daddEtFixProtein.text.toString().toInt()
            } catch (e: java.lang.NumberFormatException) {
                Log.d("ed test", "ed can't formatting")
            }
        }
        binding.daddEtFixFat.doAfterTextChanged {
            try {
                fat = binding.daddEtFixFat.text.toString().toInt()
            } catch (e: java.lang.NumberFormatException) {
                Log.d("ed test", "ed can't formatting")
            }
        }
    }

    suspend fun updateDiet() {
        dietDB!!.DietDAO().updateDietById(
            diet.id!!,
            FOOD_CLASSES[FOOD_NAMES_KR.indexOf(foodName)],
            weight.toDouble(),
            calorie.toDouble(),
            carbohydrate.toDouble(),
            protein.toDouble(),
            fat.toDouble()
        )
    }

    suspend fun deleteDiet() {
        dietDB!!.DietDAO().deleteRecord(diet.id!!)
    }

    override fun onBackPressed() {
        if (binding.daddTvTitle.text == "식단정보 변경") {
            if (fromHistory) {
                val intent = Intent(this, DReportActivity::class.java)
                intent.putExtra("DATE", diet.dDate.toString())
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("VIEW_PAGER_INDEX", 1)
                intent.putExtra("DATE", diet.dDate.toString())
                startActivity(intent)
                finish()
            }
        } else {
            val intent = Intent(this, DAddTypeSelectActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}