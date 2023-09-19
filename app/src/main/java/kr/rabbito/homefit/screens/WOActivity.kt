package kr.rabbito.homefit.screens

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kr.rabbito.homefit.R
import kr.rabbito.homefit.data.Workout
import kr.rabbito.homefit.databinding.ActivityWoBinding
import kr.rabbito.homefit.screens.workoutView.WorkoutView
import kr.rabbito.homefit.utils.calc.Converter.Companion.timeFormatter
import kr.rabbito.homefit.utils.calc.TimeCalc.Companion.milliSecFormat
import kr.rabbito.homefit.workout.WorkoutCore
import kr.rabbito.homefit.workout.WorkoutData
import kr.rabbito.homefit.workout.WorkoutState
import kr.rabbito.homefit.workout.logics.SquatPose
import kr.rabbito.homefit.workout.poseDetection.CameraSource
import kr.rabbito.homefit.workout.poseDetection.PoseDetectorProcessor
import kr.rabbito.homefit.workout.poseDetection.PreferenceUtils
import kr.rabbito.homefit.workout.tts.PoseAdviceTTS
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime

class WOActivity : AppCompatActivity() {
    companion object {
        private const val POSE_DETECTION = "Pose Detection"
        private const val TAG = "WOActivity"
    }

    private var mBinding: ActivityWoBinding? = null
    private val binding get() = mBinding!!
    private var cameraSource: CameraSource? = null
    private var selectedModel = POSE_DETECTION
    private lateinit var mat: android.opengl.Matrix
    private var workoutState = WorkoutState()
    private lateinit var countdownTimer: CountDownTimer
    private var restStartTime = 0L
    private var workoutIdx = 0
    private val woStartTime = LocalDateTime.now().format(timeFormatter)

    private lateinit var tts: PoseAdviceTTS
    private var ttsHandler: Handler? = null
    private var ttsRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 음성 안내 객체 생성
        tts = PoseAdviceTTS(this)

        createCameraSource(selectedModel)
        cameraSource?.setFacing(CameraSource.CAMERA_FACING_FRONT)

        /*
        구상: 특정 운동 타일 선택해 넘어오면, intent 이용해서 운동 인덱스 전달됨 -> 해당 인덱스로 initView 호출
         */

        // 임시
        workoutIdx = intent.getIntExtra("workoutIndex", 0)

        // 운동에 맞게 화면 초기화, 위젯 제시
        initView(workoutIdx)

        startTimer()
        binding.woPvPreviewView.stop()
        startCameraSource()

        // 음성 출력 간격 조절
        startTTSDelay()

        // 운동 정지 버튼
        binding.woBtnPause.setOnClickListener {
            WorkoutState.rest = !WorkoutState.rest
            if (WorkoutState.rest) {  // 휴식 상태일 때
                countdownTimer.cancel() // 타이머 일시정지
                restStartTime = System.currentTimeMillis()
                WorkoutState.remainSec.value =
                    120 * 1000L - WorkoutState.elapSec.value!! // '남은시간' = 2분 - '경과시간'

                binding.woBtnPause.setBackgroundResource(R.drawable.wo_btn_play)
                binding.woClPause.visibility = View.VISIBLE
                onPause()
            } else {
                WorkoutState.totalRestTime += System.currentTimeMillis() - restStartTime    // 총 휴식시간 계산
                startTimer()    // 타이머 재개

                binding.woClPause.visibility = View.INVISIBLE
                binding.woBtnPause.setBackgroundResource(R.drawable.wo_btn_pause)
                onResume()
            }
        }
        // 운동 종료 버튼
        binding.woBtnStop.setOnClickListener {
            if (WorkoutState.rest) {
                Log.d("totalRestTime1", WorkoutState.totalRestTime.toString())
                WorkoutState.totalRestTime += System.currentTimeMillis() - restStartTime
                Log.d("totalRestTime2", WorkoutState.totalRestTime.toString())
            }
            countdownTimer.cancel()
            startNextActivity()
        }

        // 임시 카운트 증가 버튼
//        binding.woBtnCount.setOnClickListener {
//            WorkoutState.count += 1
//            WorkoutState.totalCount += 1
//            SquatPose(this,tts).checkSetCondition()
//        }

        // 운동 세트 충족시 운동 종료
        WorkoutState.mySet.observe(this, Observer {
            if (it == WorkoutState.setTotal + 1) {
                countdownTimer.cancel()
                startNextActivity()
            }
        })
    }

    private fun createCameraSource(model: String) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = CameraSource(this, binding.woGoGraphicOverlay)
        }
        try {

            //POSE_DETECTION ->

            val poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
            Log.i(TAG, "Using Pose Detector with options $poseDetectorOptions")
            val shouldShowInFrameLikelihood =
                PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this)
            val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this)
            val rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this)
            val runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this)
            cameraSource!!.setMachineLearningFrameProcessor(
                PoseDetectorProcessor(
                    this,
                    poseDetectorOptions,
                    shouldShowInFrameLikelihood,
                    visualizeZ,
                    rescaleZ,
                    runClassification,
                    /* isStreamMode = */
                    true,
                    binding,
                    workoutIdx,
                    tts
                )
            )

            //}
            /*SELFIE_SEGMENTATION -> {
                cameraSource!!.setMachineLearningFrameProcessor(SegmenterProcessor(this))
            }*/
            //else -> Log.d("debug", "Unknown model")

        } catch (e: Exception) {
            Log.e(TAG, "Can not create image processor: ", e)
            Toast.makeText(
                applicationContext,
                "Can not create image processor: " + e.message,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private fun startCameraSource() {
        if (cameraSource != null) {
            try {
                binding.woPvPreviewView.start(cameraSource!!, binding.woGoGraphicOverlay)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource!!.release()
                cameraSource = null
            }
        }
    }

    // 최초 화면 초기화
    private fun initView(workoutIdx: Int) {
        /*
        구상: 운동 선택 -> 선택한 운동에 해당하는 위젯들을 생성하는 함수(generateWidgets) 실행
         */

        // 선택한 운동에 맞게 위젯 로드
        val workoutView: WorkoutView = WorkoutCore(this, binding, tts).workoutViews[workoutIdx]
        workoutView.generateWidgets()

        // 기본 위젯 로드
        Log.d("index", workoutIdx.toString())
        WorkoutState.initWorkoutState()

        binding.woTvTitle.text = WorkoutData.workoutNamesKOR[workoutIdx]
        binding.woTvSet.text = WorkoutState.setTotal.toString()
        binding.woTvCount.text = WorkoutState.count.toString()
//        val elapTime = TimeCalc.secToHourMinSec(WorkoutState.elapSec.value!!)
//        binding.woTvElapTime.text =
//            String.format("%02d:%02d:%02d", elapTime[0], elapTime[1], elapTime[2])
//        val remainTime = TimeCalc.secToHourMinSec(WorkoutState.remainSec.value!!)
//        binding.woTvRemainTime.text =
//            String.format("%02d:%02d:%02d", remainTime[0], remainTime[1], remainTime[2])
    }

    //    private fun startTimer() {
//        timer(period = 1000) {
//            runOnUiThread {
//                Log.d("타이머","$debugging")
//                if (WorkoutState.remainSec.value!! > 0) WorkoutState.remainSec.value = WorkoutState.remainSec.value!! - 1
//                WorkoutState.elapSec.value = WorkoutState.elapSec.value?.plus(1)
//
//                val elapTime = TimeCalc.secToHourMinSec(WorkoutState.elapSec.value!!)
//                binding.woTvElapTime.text =
//                    String.format("%02d:%02d:%02d", elapTime[0], elapTime[1], elapTime[2])
//                val remainTime = TimeCalc.secToHourMinSec(WorkoutState.remainSec.value!!)
//                binding.woTvRemainTime.text =
//                    String.format("%02d:%02d:%02d", remainTime[0], remainTime[1], remainTime[2])
//            }
//        }
//    }
    private fun startTimer() {
        val timeFormat = SimpleDateFormat("HH:mm:ss")

        countdownTimer = object : CountDownTimer(WorkoutState.remainSec.value!!, 1000) {
            override fun onTick(millisUntilFinished: Long) {    // 1초마다 호출되는 함수
                // 기능1 : 남은시간, 경과시간 계산
                // 기능2 : 남은시간, 경과시간 텍스트 수정
                WorkoutState.remainSec.value = millisUntilFinished
                binding.woTvRemainTime.text = milliSecFormat(WorkoutState.remainSec.value!!)
                WorkoutState.elapSec.value = WorkoutState.elapSec.value!! + 1000
                binding.woTvElapTime.text = milliSecFormat(WorkoutState.elapSec.value!!)
            }

            override fun onFinish() {   // 타이머의 시간이 종료되면 호출되는 함수
                // 기능 : 다음 액티비티로 전환
                startNextActivity()
            }
        }.start()
    }

    private fun startTTSDelay() {
        ttsHandler = Handler(Looper.getMainLooper())

        ttsRunnable = object : Runnable {
            override fun run() {
                if (WorkoutState.ttsDelay < WorkoutState.ttsDelayLimit && WorkoutState.count >= 1) WorkoutState.ttsDelay++
                ttsHandler?.postDelayed(this, 1000)
            }
        }

        if (ttsRunnable != null) {
            ttsHandler?.post(ttsRunnable!!)
        }
    }

    private fun startNextActivity() {
        val newWorkout = Workout(
            null,
            WorkoutData.workoutNamesKOR[workoutIdx],
            WorkoutState.setTotal,
            WorkoutState.totalCount,
            WorkoutState.elapSec.value,
            LocalDate.now(),
            woStartTime,
            WorkoutState.totalRestTime
        )
        val intent = Intent(this, WOReportActivity::class.java)
        intent.putExtra("index", workoutIdx)
        intent.putExtra("workout", newWorkout)

        startActivity(intent)
        finish()
    }

    public override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        createCameraSource(selectedModel)
        startCameraSource()
    }

    /** Stops the camera. */
    override fun onPause() {
        super.onPause()
        binding.woPvPreviewView.stop()
    }

    override fun onBackPressed() {
        countdownTimer.cancel()

        val intent = Intent(this,WODetailActivity::class.java)
        intent.putExtra("workoutIndex",workoutIdx)
        startActivity(intent)
        finish()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (cameraSource != null) {
            cameraSource?.release()
        }
        tts.finish()
    }

    override fun finish() {
        super.finish()
        if (ttsRunnable != null) {
            ttsHandler?.removeCallbacks(ttsRunnable!!)
        }
    }
}