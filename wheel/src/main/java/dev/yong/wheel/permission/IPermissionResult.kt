package dev.yong.wheel.permission

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions

/**
 * 注册权限申请
 * <P>
 *     注：该方法需要在onStart之前调用
 * </P>
 */
fun ComponentActivity.registerPermissionResult(permissionResult: IPermissionResult)
        : ActivityResultLauncher<Array<String>> {
    return this.registerForActivityResult(
        RequestMultiplePermissions()
    ) { permissionResult.onPermissionResult(it) }
}

/**
 * 权限申请结果接口类
 * @author coderyong
 */
interface IPermissionResult {

    /**
     * 权限申请结果回调
     * @param grantedResult 权限授予结果
     */
    fun onPermissionResult(grantedResult: Map<String, Boolean>)
}