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

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import com.example.posedetctor.GraphicOverlay
import com.google.android.gms.common.images.Size
import java.io.IOException

/** Preview the camera image in the screen.  */
class CameraSourcePreview(context: Context, attrs: AttributeSet?) :
    ViewGroup(context, attrs) {
    private val surfaceView: SurfaceView
    private var startRequested = false
    private var surfaceAvailable = false
    private var cameraSource: CameraSource? = null
    private var overlay: GraphicOverlay? = null
    @Throws(IOException::class)
    private fun start(cameraSource: CameraSource) {
        this.cameraSource = cameraSource
        if (this.cameraSource != null) {
            startRequested = true
            startIfReady()
        }
    }

    @Throws(IOException::class)
    fun start(cameraSource: CameraSource, overlay: GraphicOverlay?) {
        this.overlay = overlay
        start(cameraSource)
    }

    fun stop() {
        if (cameraSource != null) {
            cameraSource!!.stop()
        }
    }

    fun release() {
        if (cameraSource != null) {
            cameraSource!!.release()
            cameraSource = null
        }
        surfaceView.holder.surface.release()
    }

    @Throws(IOException::class, SecurityException::class)
    private fun startIfReady() {
        if (startRequested && surfaceAvailable) {
            if (PreferenceUtils.isCameraLiveViewportEnabled(context)) {
                cameraSource?.start(surfaceView.holder)
            } else {
                cameraSource?.start()
            }
            requestLayout()
            if (overlay != null) {
                val size: Size? = cameraSource?.getPreviewSize()
                val min = size?.let { Math.min(it.width, size.height) }
                val max = size?.let { Math.max(it.width, size.height) }
                val isImageFlipped =
                    cameraSource?.getCameraFacing() == CameraSource.CAMERA_FACING_FRONT
                if (isPortraitMode) {
                    // Swap width and height sizes when in portrait, since it will be rotated by 90 degrees.
                    // The camera preview and the image being processed have the same size.
                    if (min != null) {
                        if (max != null) {
                            overlay!!.setImageSourceInfo(min, max, isImageFlipped)
                        }
                    }
                    else Log.d("test","cameraSorecePreview.kt - min is Null")
                } else {
                    if (max != null) {
                        if (min != null) {
                            overlay!!.setImageSourceInfo(max, min, isImageFlipped)
                        }
                    }
                    else Log.d("test","cameraSorecePreview.kt - min is Null")
                }
                overlay!!.clear()
            }
            startRequested = false
        }
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surface: SurfaceHolder) {
            surfaceAvailable = true
            try {
                startIfReady()
            } catch (e: IOException) {
                Log.e(TAG, "Could not start camera source.", e)
            }
        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            surfaceAvailable = false
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var width = 320
        var height = 240
        if (cameraSource != null) {
            val size: Size? = cameraSource!!.getPreviewSize()
            if (size != null) {
                width = size.width
                height = size.height
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode) {
            val tmp = width
            width = height
            height = tmp
        }
        val previewAspectRatio = width.toFloat() / height
        val layoutWidth = right - left
        val layoutHeight = bottom - top
        val layoutAspectRatio = layoutWidth.toFloat() / layoutHeight
        if (previewAspectRatio > layoutAspectRatio) {
            // The preview input is wider than the layout area. Fit the layout height and crop
            // the preview input horizontally while keep the center.
            val horizontalOffset = (previewAspectRatio * layoutHeight - layoutWidth).toInt() / 2
            surfaceView.layout(-horizontalOffset, 0, layoutWidth + horizontalOffset, layoutHeight)
        } else {
            // The preview input is taller than the layout area. Fit the layout width and crop the preview
            // input vertically while keep the center.
            val verticalOffset = (layoutWidth / previewAspectRatio - layoutHeight).toInt() / 2
            surfaceView.layout(0, -verticalOffset, layoutWidth, layoutHeight + verticalOffset)
        }
    }

    private val isPortraitMode: Boolean
        private get() {
            val orientation = context.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                return false
            }
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                return true
            }
            Log.d(TAG, "isPortraitMode returning false by default")
            return false
        }

    companion object {
        private const val TAG = "MIDemoApp:Preview"
    }

    init {
        surfaceView = SurfaceView(context)
        surfaceView.holder.addCallback(SurfaceCallback())
        addView(surfaceView)
    }
}