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

class RoutineActivity : AppCompatActivity() {
    private var mBinding: ActivityRoutineBinding? = null
    private val binding get() = mBinding!!

    private var userDB: UserDB? = null
    private var userId: Long? = null
    private var routineDB: RoutineDB? = null
    private lateinit var routine: Routine

    private lateinit var routineSpinner: Spinner
    private var routineSpinnerItems: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        routineDB = Room.databaseBuilder(
            this, RoutineDB::class.java, "routine"
        ).build()

        routine = Routine(0, null, null, null, null, null)

        routineSpinner = findViewById<Spinner>(R.id.routine_btn_album)
        routineSpinnerItems = resources.getStringArray(R.array.routine_btn_album_items)
        routineSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, routineSpinnerItems ?: emptyArray()).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

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
            routine.setName = binding.routineEtSetName.text.toString() // 세트 이름 가져오기

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