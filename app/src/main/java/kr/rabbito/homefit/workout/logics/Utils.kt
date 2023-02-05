package kr.rabbito.homefit.workout.logics

import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs
import kotlin.math.atan2

fun getAngle(
    firstPoint: PoseLandmark?,
    midPoint: PoseLandmark?,
    lastPoint: PoseLandmark?
): Double {
    if (firstPoint != null && midPoint != null && lastPoint != null) {
        var result = Math.toDegrees(
            (atan2(
                lastPoint.position.y - midPoint.position.y,
                lastPoint.position.x - midPoint.position.x
            )
                    - atan2(
                firstPoint.position.y - midPoint.position.y,
                firstPoint.position.x - midPoint.position.x
            )).toDouble()
        )
        result = abs(result) // Angle should never be negative
        if (result > 180) {
            result = 360.0 - result // Always get the acute representation of the angle
        }
        return result
    } else {
        return Double.MIN_VALUE
    }
}

fun getXDistance(
    firstPoint: PoseLandmark?,
    secondPoint: PoseLandmark?,
): Float {
    if (firstPoint != null && secondPoint != null) {
        return firstPoint.position.x - secondPoint.position.x
    } else {
        return Float.MIN_VALUE
    }
}

fun getYDistance(
    firstPoint: PoseLandmark?,
    secondPoint: PoseLandmark?,
): Float {
    if (firstPoint != null && secondPoint != null) {
        return firstPoint.position.y - secondPoint.position.y
    } else {
        return Float.MIN_VALUE
    }
}

