package kr.rabbito.homefit.workout

import android.content.Context
import android.util.Log
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs
import kotlin.math.atan2

class PoseTest {
        lateinit var pose : Pose
        lateinit var context : Context

        public fun getAngle(firstPoint: PoseLandmark, midPoint: PoseLandmark, lastPoint: PoseLandmark): Double {
            var result = Math.toDegrees(
                (atan2(lastPoint.position.y - midPoint.position.y,
                    lastPoint.position.x - midPoint.position.x)
                        - atan2(firstPoint.position.y - midPoint.position.y,
                    firstPoint.position.x - midPoint.position.x)).toDouble()
            )
            result = abs(result) // Angle should never be negative
            if (result > 180) {
                result = 360.0 - result // Always get the acute representation of the angle
            }
            return result
        }
    public fun calcKneeDrive(pose : Pose) {
            Log.d("pose test","call calcKneeDrive")

            var nose = pose.getPoseLandmark(PoseLandmark.NOSE)
            //Log.d("pose test :", "$nose")
            //Log.d("nose position :","${nose!!.position3D.x}, ${nose.position3D.y}")

            var rightHand = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
            var rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
            var rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
            //Log.d("Hand Position :","${rightHand!!.position3D.x}, ${rightHand.position3D.y}")
            //Log.d("Elbow Position :","${rightElbow!!.position3D.x}, ${rightElbow.position3D.y}")
            //Log.d("Shoulder Position :","${rightShoulder!!.position3D.x}, ${rightShoulder.position3D.y}")

            try{
                var rightArmAngle = getAngle(rightHand!!,rightElbow!!,rightShoulder!!)
                //Log.d("Angle test","$rightArmAngle")
                if(rightArmAngle>90){
                    Log.d("Angle test","over 90`")
                }else{
                    Log.d("Angle test","under 90`")
                }
            }catch(e : NullPointerException){
                return
            }
        }
    }

