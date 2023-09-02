/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.rabbito.homefit.workout.poseDetection

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.google.common.primitives.Ints
import com.example.posedetctor.GraphicOverlay
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import java.lang.Math.max
import java.lang.Math.min
import java.util.Locale

/** Draw the detected pose in preview.  */
class PoseGraphic internal constructor(
  overlay: GraphicOverlay,
  private val pose: Pose,
  private val showInFrameLikelihood: Boolean,
  private val visualizeZ: Boolean,
  private val rescaleZForVisualization: Boolean,
  private val poseClassification: List<String>
) : GraphicOverlay.Graphic(overlay) {
  private var zMin = java.lang.Float.MAX_VALUE
  private var zMax = java.lang.Float.MIN_VALUE
  private val classificationTextPaint: Paint

  companion object {
    private val DOT_RADIUS = 8.0f
    private val IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f
    private val STROKE_WIDTH = 10.0f
    private val POSE_CLASSIFICATION_TEXT_SIZE = 60.0f

    val whitePaint = Paint()
    val greenPaint = Paint()
    val yellowPaint = Paint()
    val redPaint = Paint()

    /*
    <구상>
    외부에서 PoseGraphic 호출하면서, leftShoulderToLeftElbowPaint를 변경 -> 화면에 출력되는 안내선 색상 변경
    ex) 자세가 잘못되었으면 leftShoulderToLeftElbowPaint = redPaint로 변경
     */

    // 아래의 변수들 조정해 안내선 색상 변경 - 기본: 흰색

    // left paint
    var leftShoulderToLeftElbowPaint = whitePaint
    var leftElbowToLeftWristPaint = whitePaint
    var leftShoulderToLeftHipPaint = whitePaint
    var leftHipToLeftKneePaint = whitePaint
    var leftKneeToLeftAnklePaint = whitePaint

    // right paint
    var rightShoulderToRightElbowPaint = whitePaint
    var rightElbowToRightWristPaint = whitePaint
    var rightShoulderToRightHipPaint = whitePaint
    var rightHipToRightKneePaint = whitePaint
    var rightKneeToRightAnklePaint = whitePaint
  }

  init {
    classificationTextPaint = Paint()
    classificationTextPaint.color = Color.WHITE
    classificationTextPaint.textSize = POSE_CLASSIFICATION_TEXT_SIZE
    classificationTextPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK)

    whitePaint.strokeWidth = STROKE_WIDTH
    whitePaint.color = Color.WHITE
    whitePaint.textSize = IN_FRAME_LIKELIHOOD_TEXT_SIZE

    greenPaint.strokeWidth = STROKE_WIDTH
    greenPaint.color = Color.parseColor("#A5D6A7")

    yellowPaint.strokeWidth = STROKE_WIDTH
    yellowPaint.color = Color.YELLOW

    redPaint.strokeWidth = STROKE_WIDTH * 1.3f
    redPaint.color = Color.RED
  }

  override fun draw(canvas: Canvas?) {
    val landmarks = pose.allPoseLandmarks
    if (landmarks.isEmpty()) {
      return
    }

    // Draw pose classification text.
    val classificationX = POSE_CLASSIFICATION_TEXT_SIZE * 0.5f
    for (i in poseClassification.indices) {
      val classificationY = (canvas!!.height).minus(
          (
                  POSE_CLASSIFICATION_TEXT_SIZE * 1.5f * (poseClassification.size - i).toFloat()
                  )
      )

      if (classificationY != null) {
        canvas.drawText(
          poseClassification[i],
          classificationX,
          classificationY,
          classificationTextPaint
        )
      }
    }

    // Draw all the points

    for (landmark in landmarks) {
      if (canvas != null) {
        if (landmark.landmarkType > 10) drawPoint(canvas, landmark, whitePaint)
      }
      if (visualizeZ && rescaleZForVisualization) {
        zMin = min(zMin, landmark.position3D.z)
        zMax = max(zMax, landmark.position3D.z)
      }
    }

    val nose = pose.getPoseLandmark(PoseLandmark.NOSE)
    val lefyEyeInner = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER)
    val lefyEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE)
    val leftEyeOuter = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER)
    val rightEyeInner = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER)
    val rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)
    val rightEyeOuter = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER)
    val leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR)
    val rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR)
    val leftMouth = pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH)
    val rightMouth = pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH)

    val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
    val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
    val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
    val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
    val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
    val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
    val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
    val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
    val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
    val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
    val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
    val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

    val leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
    val rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
    val leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
    val rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
    val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
    val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
    val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
    val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
    val leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
    val rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)


//    // Face
//    drawLine(canvas!!, nose, lefyEyeInner, whitePaint)
//    drawLine(canvas, lefyEyeInner, lefyEye, whitePaint)
//    drawLine(canvas, lefyEye, leftEyeOuter, whitePaint)
//    drawLine(canvas, leftEyeOuter, leftEar, whitePaint)
//    drawLine(canvas, nose, rightEyeInner, whitePaint)
//    drawLine(canvas, rightEyeInner, rightEye, whitePaint)
//    drawLine(canvas, rightEye, rightEyeOuter, whitePaint)
//    drawLine(canvas, rightEyeOuter, rightEar, whitePaint)
//    drawLine(canvas, leftMouth, rightMouth, whitePaint)

    drawLine(canvas!!, leftShoulder, rightShoulder, whitePaint)
    drawLine(canvas, leftHip, rightHip, whitePaint)

    /*
    구상: 루프 돌 때마다 변경된 leftShoulderToLeftElbowPaint가 적용되도록 구현
     */

    // Left body
    drawLine(canvas, leftShoulder, leftElbow, leftShoulderToLeftElbowPaint)
    drawLine(canvas, leftElbow, leftWrist, leftElbowToLeftWristPaint)
    drawLine(canvas, leftShoulder, leftHip, leftShoulderToLeftHipPaint)
    drawLine(canvas, leftHip, leftKnee, leftHipToLeftKneePaint)
    drawLine(canvas, leftKnee, leftAnkle, leftKneeToLeftAnklePaint)
    drawLine(canvas, leftWrist, leftThumb, whitePaint)
    drawLine(canvas, leftWrist, leftPinky, whitePaint)
    drawLine(canvas, leftWrist, leftIndex, whitePaint)
    drawLine(canvas, leftIndex, leftPinky, whitePaint)
    drawLine(canvas, leftAnkle, leftHeel, whitePaint)
    drawLine(canvas, leftHeel, leftFootIndex, whitePaint)

    // Right body
    drawLine(canvas, rightShoulder, rightElbow, rightShoulderToRightElbowPaint)
    drawLine(canvas, rightElbow, rightWrist, rightElbowToRightWristPaint)
    drawLine(canvas, rightShoulder, rightHip, rightShoulderToRightHipPaint)
    drawLine(canvas, rightHip, rightKnee, rightHipToRightKneePaint)
    drawLine(canvas, rightKnee, rightAnkle, rightKneeToRightAnklePaint)
    drawLine(canvas, rightWrist, rightThumb, whitePaint)
    drawLine(canvas, rightWrist, rightPinky, whitePaint)
    drawLine(canvas, rightWrist, rightIndex, whitePaint)
    drawLine(canvas, rightIndex, rightPinky, whitePaint)
    drawLine(canvas, rightAnkle, rightHeel, whitePaint)
    drawLine(canvas, rightHeel, rightFootIndex, whitePaint)

    // Draw inFrameLikelihood for all points
    if (showInFrameLikelihood) {
      for (landmark in landmarks) {
        canvas.drawText(
          String.format(Locale.US, "%.2f", landmark.inFrameLikelihood),
          translateX(landmark.position.x),
          translateY(landmark.position.y),
          whitePaint
        )
      }
    }
  }

  internal fun drawPoint(canvas: Canvas, landmark: PoseLandmark, paint: Paint) {
    val point = landmark.position3D
    maybeUpdatePaintColor(paint, canvas, point.z)
    canvas.drawCircle(translateX(point.x), translateY(point.y), DOT_RADIUS, paint)
  }

  internal fun drawLine(
    canvas: Canvas,
    startLandmark: PoseLandmark?,
    endLandmark: PoseLandmark?,
    paint: Paint
  ) {
    val start = startLandmark!!.position3D
      val end = endLandmark!!.position3D

    // Gets average z for the current body line
    val avgZInImagePixel = (start.z + end.z) / 2
    maybeUpdatePaintColor(paint, canvas, avgZInImagePixel)

    canvas.drawLine(
        translateX(start.x),
        translateY(start.y),
        translateX(end.x),
        translateY(end.y),
        paint
      )
  }

  internal fun maybeUpdatePaintColor(
    paint: Paint,
    canvas: Canvas,
    zInImagePixel: Float
  ) {
    if (!visualizeZ) {
      return
    }

    // When visualizeZ is true, sets up the paint to different colors based on z values.
    // Gets the range of z value.
    val zLowerBoundInScreenPixel: Float
    val zUpperBoundInScreenPixel: Float

    if (rescaleZForVisualization) {
      zLowerBoundInScreenPixel = min(-0.001f, scale(zMin))
      zUpperBoundInScreenPixel = max(0.001f, scale(zMax))
    } else {
      // By default, assume the range of z value in screen pixel is [-canvasWidth, canvasWidth].
      val defaultRangeFactor = 1f
      zLowerBoundInScreenPixel = -defaultRangeFactor * canvas.width
      zUpperBoundInScreenPixel = defaultRangeFactor * canvas.width
    }

    val zInScreenPixel = scale(zInImagePixel)

    if (zInScreenPixel < 0) {
      // Sets up the paint to draw the body line in red if it is in front of the z origin.
      // Maps values within [zLowerBoundInScreenPixel, 0) to [255, 0) and use it to control the
      // color. The larger the value is, the more red it will be.
      var v = (zInScreenPixel / zLowerBoundInScreenPixel * 255).toInt()
      v = Ints.constrainToRange(v, 0, 255)
      paint.setARGB(255, 255, 255 - v, 255 - v)
    } else {
      // Sets up the paint to draw the body line in blue if it is behind the z origin.
      // Maps values within [0, zUpperBoundInScreenPixel] to [0, 255] and use it to control the
      // color. The larger the value is, the more blue it will be.
      var v = (zInScreenPixel / zUpperBoundInScreenPixel * 255).toInt()
      v = Ints.constrainToRange(v, 0, 255)
      paint.setARGB(255, 255 - v, 255 - v, 255)
    }
  }
}
