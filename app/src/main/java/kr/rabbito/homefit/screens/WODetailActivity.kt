package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import kr.rabbito.homefit.data.Routine
import kr.rabbito.homefit.databinding.ActivityWodetailBinding
import kr.rabbito.homefit.workout.WorkoutData
import kr.rabbito.homefit.workout.WorkoutState

class WODetailActivity : AppCompatActivity() {
    private var mBinding: ActivityWodetailBinding? = null
    private val binding get() = mBinding!!

    private var Screen_Type = "from_WODetailActivity"
    private lateinit var routine: Routine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWodetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val workoutIndex = intent.getIntExtra("workoutIndex", 0)

        try { // 세트 불러오기 정보 적용
            routine = (intent.getSerializableExtra("routine") as Routine?)!!
            var count = binding.wodetailEtSetCount
            Log.e("setnum",routine.set.toString() )
            routine.set?.let { count.setText(it.toString()) } //

            var rest = binding.wodetailEtRestCount
            routine.restTime?.let { rest.setText(it.toString()) }
        } catch (e: NullPointerException) {
            // routine이 null인 경우에 대한 처리
            // 예를 들어, 초기 값을 설정하거나 에러 메시지를 표시할 수 있습니다.
            Log.e("null routine", "routine 객체를 받지 않음")
        } catch (e: Exception) {
            // Parcelable 객체 읽기 실패에 대한 처리
            // 예를 들어, 초기 값을 설정하거나 에러 메시지를 표시할 수 있습니다.
            Log.e("parcelable error", "Parcelable 객체 읽기 실패")
        }

        binding.wodetailTvTitle.text = WorkoutData.workoutNamesKOR[workoutIndex]
        binding.wodetailTvSubTitle.text = WorkoutData.workoutNamesENG[workoutIndex]
        binding.wodetailIvWo.setImageResource(WorkoutData.workoutImages[workoutIndex])
        binding.wodetailTvDetail.text = WorkoutData.workoutExplain[workoutIndex]

        if (workoutIndex == 3) {
            binding.wodetailTvTitle.textSize = 22F
            binding.wodetailTvSubTitle.textSize = 10F
        }
        // WorkoutState.setTotal = 3


        binding.wodetailBtnStartWo.setOnClickListener {
            // 임시
            // 운동 시작 버튼
            val intent = Intent(this, WOActivity::class.java)
            intent.putExtra("workoutIndex", workoutIndex)
            startActivity(intent)
        }
        binding.wodetailBtnLoad.setOnClickListener {
            // 세트 불러오기 버튼
            val intent = Intent(this, RoutineListActivity::class.java)
            intent.putExtra("Starting Point", Screen_Type) // 화면 시작 위치
            intent.putExtra("workoutIndex", workoutIndex)
            startActivity(intent)
        }
        binding.wodetailBtnSetCountSub.setOnClickListener{
            // 세트 감소버튼

            var count = binding.wodetailEtSetCount
            if(!count.text.isNullOrEmpty() && count.text.toString().toInt() > 0) {
                count.setText((count.text.toString().toInt()-1).toString())
            }
            else{
                Toast.makeText(this, "더 낮출 수 없습니다",Toast.LENGTH_SHORT).show()
            }
            WorkoutState.setTotal = count.text.toString().toInt()
        }
        binding.wodetailBtnSetCountAdd.setOnClickListener {
            // 세트 증가버튼

            var count = binding.wodetailEtSetCount
            if(!count.text.isNullOrEmpty()){
                count.setText((count.text.toString().toInt()+1).toString())
            }
            else{
                count.setText("1")
            }
            WorkoutState.setTotal = count.text.toString().toInt()
        }
        binding.wodetailBtnRestCountSub.setOnClickListener {
            // 휴식 감소버튼

            var rest = binding.wodetailEtRestCount
            if(!rest.text.isNullOrEmpty() && rest.text.toString().toInt() > 0) {
                rest.setText((rest.text.toString().toInt()-1).toString())
            }
            else{
                Toast.makeText(this, "더 낮출 수 없습니다",Toast.LENGTH_SHORT).show()
            }
            WorkoutState.restTotal = rest.text.toString().toInt()
        }
        binding.wodetailBtnRestCountAdd.setOnClickListener {
            // 휴식 증가버튼

            var rest = binding.wodetailEtRestCount
            if(!rest.text.isNullOrEmpty()){
                rest.setText((rest.text.toString().toInt()+1).toString())
            }
            else{
                rest.setText("1")
            }
            WorkoutState.restTotal = rest.text.toString().toInt()
        }
        binding.wodetailEtSetCount.doAfterTextChanged {
            try {
                WorkoutState.setTotal = binding.wodetailEtSetCount.text.toString().toInt()
            }catch (e : java.lang.NumberFormatException){
                Log.d("ed test","ed can't formatting")
            }
        }
        binding.wodetailEtRestCount.doAfterTextChanged {
            try {
                WorkoutState.restTotal = binding.wodetailEtRestCount.text.toString().toInt()
            }catch (e : java.lang.NumberFormatException){
                Log.d("ed test","ed can't formatting")
            }
        }

        binding.wodetailBtnBack.setOnClickListener {
            finish()
        }
    }
}