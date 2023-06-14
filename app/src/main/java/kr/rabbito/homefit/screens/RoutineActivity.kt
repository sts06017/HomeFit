package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.rabbito.homefit.R
import kr.rabbito.homefit.data.Routine
import kr.rabbito.homefit.data.RoutineDB
import kr.rabbito.homefit.data.UserDB
import kr.rabbito.homefit.databinding.ActivityRoutineBinding
import kr.rabbito.homefit.screens.navigatorBar.SettingFragment
import kr.rabbito.homefit.workout.WorkoutState
import kotlin.properties.Delegates

class RoutineActivity : AppCompatActivity() {
    private var mBinding: ActivityRoutineBinding? = null
    private val binding get() = mBinding!!

    private var userDB: UserDB? = null
    private var userId: Long? = null
    private var routineDB: RoutineDB? = null
    private lateinit var routine: Routine

    private lateinit var routineSpinner: Spinner
    private var routineSpinnerItems: Array<String>? = null
    private lateinit var spinnerRoutineText: String
    private var routineId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        routineDB = Room.databaseBuilder(
            this, RoutineDB::class.java, "routine"
        ).build()

        routineSpinner = findViewById<Spinner>(R.id.routine_btn_album)
        routineSpinnerItems = resources.getStringArray(R.array.routine_btn_album_items)
        routineSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, routineSpinnerItems ?: emptyArray()).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        try { // 세트 불러오기 정보 적용
            routine = (intent.getSerializableExtra("routine") as Routine?)!!
            if(routine != null){ // 받아온 routine 객체가 존재할 시 객체 정보 적용
                binding.routineEtSetName.setText(routine.setName.toString())
                binding.routineEtSetCount.setText(routine.set.toString())
                binding.routineEtRestCount.setText(routine.restTime.toString())

                spinnerRoutineText = routine.workoutName.toString() // 스피너 내용 변경
                for (i in 0 until routineSpinner.adapter.count) {
                    val item = routineSpinner.adapter.getItem(i)
                    if (item.toString() == spinnerRoutineText) {
                        routineSpinner.setSelection(i)
                        break
                    }
                }
                routineId = routine.id!!
            }
            binding.routineTvTitle.setText("세트설정 변경")
            binding.routineBtnAddSet.setText("세트설정 변경")
        } catch (e: NullPointerException) {
            // routine이 null인 경우에 대한 처리
            // 예를 들어, 초기 값을 설정하거나 에러 메시지를 표시할 수 있습니다.
            binding.routineTvTitle.setText("세트설정 추가")
            binding.routineBtnAddSet.setText("세트설정 추가")
            Log.e("null routine", "routine 객체를 받지 않음")
        } catch (e: Exception) {
            // Parcelable 객체 읽기 실패에 대한 처리
            // 예를 들어, 초기 값을 설정하거나 에러 메시지를 표시할 수 있습니다.
            Log.e("parcelable error", "Parcelable 객체 읽기 실패")
        }

        routine = Routine(0, null, null, null, null, null)

        routineSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = routineSpinnerItems?.get(position)
                // 선택된 항목 처리
                selectedItem?.let {
                    routine.workoutName = it
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 선택된 항목이 없을 때 수행할 동작 정의
            }
        }
        
        binding.routineBtnBack.setOnClickListener {// 뒤로 가기 버튼
            startActivity(Intent(this, RoutineListActivity::class.java))
        }

        binding.routineBtnSetCountAdd.setOnClickListener {
            // 세트 증가버튼

            var count = binding.routineEtSetCount
            if(!count.text.isNullOrEmpty()){
                count.setText((count.text.toString().toInt()+1).toString())
                routine.set = (count.text.toString().toInt())
            }
            else{
                count.setText("1")
                routine.set = 1
            }
            WorkoutState.setTotal = count.text.toString().toInt()
        }

        binding.routineBtnSetCountSub.setOnClickListener{
            // 세트 감소버튼

            var count = binding.routineEtSetCount
            if(!count.text.isNullOrEmpty() && count.text.toString().toInt() > 0) {
                count.setText((count.text.toString().toInt()-1).toString())
                routine.set = (count.text.toString().toInt())
            }
            else{
                Toast.makeText(this, "더 낮출 수 없습니다", Toast.LENGTH_SHORT).show()
            }
            WorkoutState.setTotal = count.text.toString().toInt()
        }

        binding.routineBtnRestCountAdd.setOnClickListener {
            // 휴식 증가버튼

            var rest = binding.routineEtRestCount
            if(!rest.text.isNullOrEmpty()){
                rest.setText((rest.text.toString().toInt()+1).toString())
                routine.restTime = rest.text.toString().toLong()
            }
            else{
                rest.setText("1")
                routine.restTime = 1
            }
            WorkoutState.restTotal = rest.text.toString().toInt()
        }

        binding.routineBtnRestCountSub.setOnClickListener {
            // 휴식 감소버튼

            var rest = binding.routineEtRestCount
            if(!rest.text.isNullOrEmpty() && rest.text.toString().toInt() > 0) {
                rest.setText((rest.text.toString().toInt()-1).toString())
                routine.restTime = rest.text.toString().toLong()
            }
            else{
                Toast.makeText(this, "더 낮출 수 없습니다",Toast.LENGTH_SHORT).show()
            }
            WorkoutState.restTotal = rest.text.toString().toInt()
        }

        binding.routineBtnAddSet.setOnClickListener {
            // 세트 추가 버튼
            if(binding.routineBtnAddSet.text == "세트설정 변경"){ // 세트 수정
                routine.id = routineId
                routine.setName = binding.routineEtSetName.text.toString()
                routine.set = (binding.routineEtSetCount.text.toString().toInt())
                routine.restTime = (binding.routineEtRestCount.text.toString().toLong())
                CoroutineScope(Dispatchers.IO).launch {// DB에 세트 추가
                    routineDB!!.routineDAO().update(routine)
                }
                startActivity(Intent(this, RoutineListActivity::class.java))
            }else{ // 세트 추가
                val newRoutine = Routine(
                    id = null,
                    setName = binding.routineEtSetName.text.toString(),
                    workoutName = routine.workoutName,
                    set = routine.set,
                    count = routine.count,
                    restTime = routine.restTime
                )

                CoroutineScope(Dispatchers.IO).launch {// DB에 세트 추가
                    routineDB!!.routineDAO().insert(newRoutine)
                }
                startActivity(Intent(this, RoutineListActivity::class.java))
            }


        }
    }
}