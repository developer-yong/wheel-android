package dev.yong.sample.map

import android.os.Bundle
import android.view.View
import com.amap.api.maps.AMap
import dev.yong.sample.databinding.FragmentMapBinding
import dev.yong.wheel.base.SwipeBackFragment


class MapFragment : SwipeBackFragment<FragmentMapBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mRoot.map.onCreate(savedInstanceState)
        //初始化地图控制器对象
        val aMap: AMap = mRoot.map.map
    }

    override fun onResume() {
        super.onResume()
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mRoot.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mRoot.map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mRoot.map.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mRoot.map.onSaveInstanceState(outState)
    }
}