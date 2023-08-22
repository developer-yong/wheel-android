package dev.yong.sample

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.yong.sample.list.ListFragment
import dev.yong.wheel.Router
import dev.yong.wheel.base.ViewBindActivity
import dev.yong.wheel.databinding.LayoutContainerBinding
import dev.yong.wheel.network.NetworkReceiver
import dev.yong.wheel.permission.Permission
import dev.yong.wheel.utils.StatusBar
import dev.yong.wheel.utils.getStatusBarHeight

class MainActivity : ViewBindActivity<LayoutContainerBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        Permission.with(this)
            .check(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request(object : Permission.PermissionGrantedListener {
                override fun onGranted(granted: Boolean) {
                    if (granted) {
                        Router.with(this@MainActivity, R.id.layout_container)
                            .open(ListFragment::class.java, null)
                    }
                }
            })
    }

    override fun onBackPressed() {
        Router.popStackImmediate(this, 0, 0)
    }

}