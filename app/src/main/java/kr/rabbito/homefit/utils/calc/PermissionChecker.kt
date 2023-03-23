package kr.rabbito.homefit.utils.calc

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionChecker {
    companion object {
        fun checkPermission(context: Context, permissionList: List<String>): Boolean {
            for (i: Int in permissionList.indices) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        permissionList[i]
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    return false
                }
            }

            return true
        }

        fun requestPermission(activity: Activity, permissionList: List<String>) {
            ActivityCompat.requestPermissions(activity, permissionList.toTypedArray(), 10)
        }
    }
}