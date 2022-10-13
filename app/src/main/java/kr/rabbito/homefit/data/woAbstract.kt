package kr.rabbito.homefit.data

import com.google.mlkit.vision.pose.Pose

abstract class woAbstract(val name : String, val Set : Set,var pose : Pose){
    abstract fun Init()
    abstract fun detectWrong()
    abstract fun countReps()
}
