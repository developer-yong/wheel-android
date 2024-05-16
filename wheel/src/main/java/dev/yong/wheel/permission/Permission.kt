@file:Suppress("unused")

package dev.yong.wheel.permission

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * @author coderyong
 */
object Permission {

    private var TAG = "Permissions"

    @JvmStatic
    fun with(activity: FragmentActivity): Builder {
        return Builder(activity)
    }

    class Builder internal constructor(private var activity: FragmentActivity) {

        private var permissions: MutableList<String>? = null

        fun check(vararg permissions: String): Builder {
            if (this.permissions == null) {
                this.permissions = ArrayList()
            }
            this.permissions!!.addAll(listOf(*permissions))
            return this
        }

        fun request(listener: PermissionGrantedListener?) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (listener != null) {
                    TAG = listener.toString()
                    listener.onGranted(true)
                }
            } else {
                if (listener != null) {
                    TAG = listener.toString()
                }
                val fragment = findPermissionsFragment(activity)
                if (fragment is PermissionsFragment) {
                    permissions?.run { fragment.requestPermissions(listener, this) }
                }
            }
        }

        private fun findPermissionsFragment(activity: FragmentActivity): Fragment {
            var fragment = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = PermissionsFragment()
                val fragmentManager = activity.supportFragmentManager
                fragmentManager.beginTransaction()
                    .add(fragment, TAG)
                    .commitAllowingStateLoss()
                fragmentManager.executePendingTransactions()
            }
            return fragment
        }

    }

    @Suppress("DEPRECATION")
    class PermissionsFragment : Fragment() {

        private var mPermissionGrantedListener: PermissionGrantedListener? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        fun requestPermissions(listener: PermissionGrantedListener?, permissions: List<String>) {
            mPermissionGrantedListener = listener
            if (checkPermission(permissions)) {
                mPermissionGrantedListener?.onGranted(true)
            } else {
                //请求权限弹窗
                requestPermissions(
                    permissions.toTypedArray(),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }

        private fun checkPermission(permissions: List<String>?): Boolean {
            var isGranted = activity != null
            if (activity != null) {
                for (permission in permissions!!) {
                    if (ActivityCompat.checkSelfPermission(
                            requireActivity(), permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        isGranted = false
                        break
                    }
                }
            }
            return isGranted
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            // If request is cancelled, the result arrays are empty.
            if (requestCode == PERMISSIONS_REQUEST_CODE) {
                if (mPermissionGrantedListener != null) {
                    mPermissionGrantedListener!!.onGranted(
                        grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    )
                }
            } else {
                if (mPermissionGrantedListener != null) {
                    mPermissionGrantedListener!!.onGranted(checkPermission(listOf(*permissions)))
                }
            }
            parentFragmentManager.beginTransaction().remove(this).commit()
            parentFragmentManager.popBackStackImmediate()
        }

        override fun onDestroy() {
            super.onDestroy()
            mPermissionGrantedListener = null
        }

        companion object {
            private const val PERMISSIONS_REQUEST_CODE = 42
        }
    }

    interface PermissionGrantedListener {
        /**
         * 权限授予回调
         *
         * @param granted 是否授予
         */
        fun onGranted(granted: Boolean)
    }
}