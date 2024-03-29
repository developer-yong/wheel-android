@file:Suppress("unused")

package dev.yong.wheel.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.children
import dev.yong.wheel.R
import dev.yong.wheel.view.EmptyView

/**
 * @author coderyong
 */
class WebDelegate private constructor() {

    var argumentsInterceptor: ArgumentsInterceptor? = null
        private set

    var webClient: WebClient? = null
        private set
    var chromeClient: ChromeClient? = null
        private set

    private object WebDelegateHolder {
        val INSTANCE = WebDelegate()
    }

    /**
     * 设置参数拦截器
     */
    fun setArgumentsInterceptor(interceptor: ArgumentsInterceptor): WebDelegate {
        argumentsInterceptor = interceptor
        return this
    }

    /**
     * 设置WebViewClient
     * @see WebView#setWebViewClient(WebViewClient)
     */
    fun setWebViewClient(webClient: WebClient): WebDelegate {
        this.webClient = webClient
        return this
    }

    /**
     * 设置WebChromeClient
     * @see WebView#setWebChromeClient(WebChromeClient)
     */
    fun setWebChromeClient(chromeClient: ChromeClient): WebDelegate {
        this.chromeClient = chromeClient
        return this
    }

    interface ArgumentsInterceptor {
        /**
         * 参数拦截处理
         *
         * @param arguments Bundle
         */
        fun intercept(arguments: Bundle?): Bundle? {
            return arguments
        }
    }

    open class WebClient : WebViewClient() {

        var mNoNetworkView: View? = null

        @SuppressLint("SetJavaScriptEnabled")
        fun applySettings(webView: WebView) {
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            //自适应屏幕
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (url.endsWith(".png")) {

                // 设置滚动条不显示
                view.isHorizontalScrollBarEnabled = false
                view.isVerticalScrollBarEnabled = false

                view.loadUrl(
                    "javascript:function setBackground(){" +
                            "document.getElementsByTagName('body')[0].style.background='#ffffff'" +
                            "};setBackground();"
                )
            }
        }

        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            EmptyView.with(view).hide()
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            if (mNoNetworkView == null) {
                mNoNetworkView = View.inflate(view.context, R.layout.layout_no_network, null)
                mNoNetworkView!!.setOnClickListener { view.reload() }
            }
            EmptyView.with(view).show(mNoNetworkView!!)
        }
    }

    open class ChromeClient : WebChromeClient() {

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            val parent = view.parent
            if (parent is LinearLayout) {
                for (child in parent.children) {
                    if (child is ProgressBar) {
                        child.progress = newProgress
                        if (newProgress > 99) {
                            child.visibility = View.GONE
                        } else {
                            child.visibility = View.VISIBLE
                        }
                        break
                    }
                }
            }
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            val parent = view?.parent
            if (parent is LinearLayout) {
                val tvTitle = parent.findViewById<TextView>(R.id.action_title)
                if (tvTitle != null && TextUtils.isEmpty(tvTitle.text)) {
                    tvTitle.text = title
                }
            }
            super.onReceivedTitle(view, title)
        }
    }

    companion object {

        @JvmStatic
        val instance
            get() = WebDelegateHolder.INSTANCE
    }
}