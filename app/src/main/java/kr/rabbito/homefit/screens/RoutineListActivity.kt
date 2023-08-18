package kr.rabbito.homefit.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.rabbito.homefit.data.Routine
import kr.rabbito.homefit.data.RoutineDB
import kr.rabbito.homefit.databinding.ActivityRoutinelistBinding
import kr.rabbito.homefit.screens.adapter.RoutineListAdapter
import kr.rabbito.homefit.screens.navigatorBar.ProfileFragment

class RoutineListActivity : AppCompatActivity() {
    private var mBinding: ActivityRoutinelistBinding? = null
    private val binding get() = mBinding!!

    private var workoutIndex = 0
    private var Screen_Type: String = ""

    private var routine: List<Routine>? = null
    private var db: RoutineDB? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRoutinelistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Screen_Type = intent.getStringExtra("Starting Point").toString()

        if (Screen_Type == "from_WODetailActivity") { // 세트 불러오기
            workoutIndex = intent.getIntExtra("workoutIndex", 0)
            binding.routinelistTvTitle.text = "세트설정 선택"
            binding.dreportBtnAdd.visibility = View.GONE
        }else{ // 세트 추가하기
            binding.routinelistTvTitle.text = "세트설정 목록"
            binding.dreportBtnAdd.visibility = View.VISIBLE
        }

        binding.routinelistBtnBack.setOnClickListener {// 뒤로 가기 버튼
            if (Screen_Type == "from_WODetailActivity"){
                val intent = Intent(this, WODetailActivity::class.java)
                intent.putExtra("workoutIndex", workoutIndex)
                startActivity(intent)
                finish()
            } else {
                finish()
            }
        }

        binding.dreportBtnAdd.setOnClickListener {// 세트 추가 버튼
            Log.e("buttontest001", "list")
            startActivity(Intent(this, RoutineActivity::class.java))
            finish()
        }

        // 데이터베이스 빌드
        db = Room.databaseBuilder(this, RoutineDB::class.java, "routine").build()

        // 어댑터 연결 및 데이터베이스 작업
        GlobalScope.launch(Dispatchers.IO) {
            routine = db!!.routineDAO().getAll()
            withContext(Dispatchers.Main) {
                binding.routineRvList.layoutManager = LinearLayoutManager(this@RoutineListActivity)
                binding.routineRvList.adapter = RoutineListAdapter(routine!!, Screen_Type, workoutIndex)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (Screen_Type == "from_WODetailActivity") {
            val intent = Intent(this, WODetailActivity::class.java)
            intent.putExtra("workoutIndex", workoutIndex)
            startActivity(intent)
            finish()
        }
    }
}