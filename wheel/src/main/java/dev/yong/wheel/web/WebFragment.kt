package dev.yong.wheel.web

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import dev.yong.wheel.R

/**
 * WebContainer title 参数
 */
const val PARAM_WEB_TITLE = "web_title"

/**
 * WebContainer url 参数
 */
const val PARAM_WEB_URL = "web_url"

/**
 * WebContainer show_title 参数
 */
const val PARAM_SHOW_TITLE = "web_show_title"

/**
 * WebContainer title_bar_height 参数
 */
const val PARAM_TITLE_BAR_HEIGHT = "web_title_bar_height"

/**
 * WebContainer title_bar_color 参数
 */
const val PARAM_TITLE_BAR_COLOR = "web_title_bar_color"

/**
 * WebContainer show_divider 参数
 */
const val PARAM_SHOW_DIVIDER = "web_show_divider"

/**
 * WebContainer progress_height 参数
 */
const val PARAM_DIVIDER_HEIGHT = "web_divider_height"

/**
 * WebContainer progress_color 参数
 */
const val PARAM_DIVIDER_COLOR = "web_divider_color"

/**
 * WebContainer show_progress 参数
 */
const val PARAM_SHOW_PROGRESS = "web_show_progress"

/**
 * WebContainer progress_height 参数
 */
const val PARAM_PROGRESS_HEIGHT = "web_progress_height"

/**
 * WebContainer progress_color 参数
 */
const val PARAM_PROGRESS_COLOR = "web_progress_color"

/**
 * @author coderyong
 */
class WebFragment : Fragment() {

    private var mTvTitle: TextView? = null
    private var mProgressBar: ProgressBar? = null
    private lateinit var mWeb: WebView

    private var isShowTitle = true
    private var titleBarHeight = -2
    private var titleBarColor = Color.WHITE

    private var isShowDivider = true
    private var dividerHeight = -2
    private var dividerColor = Color.LTGRAY

    private var isShowProgress = true
    private var progressBarHeight = 3
    private var progressBarColor = Color.BLUE

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val interceptor = WebDelegate.instance.argumentsInterceptor
        val arguments = requireArguments()
        interceptor?.intercept(arguments)
        //获取TitleBar参数
        isShowTitle = arguments.getBoolean(PARAM_SHOW_TITLE, isShowTitle)
        titleBarHeight = arguments.getInt(PARAM_TITLE_BAR_HEIGHT, titleBarHeight)
        titleBarColor = arguments.getInt(PARAM_TITLE_BAR_COLOR, titleBarColor)
        //获取Divider参数
        isShowDivider = arguments.getBoolean(PARAM_SHOW_DIVIDER, isShowDivider)
        dividerHeight = arguments.getInt(PARAM_DIVIDER_HEIGHT, dividerHeight)
        dividerColor = arguments.getInt(PARAM_DIVIDER_COLOR, dividerColor)
        //获取ProgressBar参数
        isShowProgress = arguments.getBoolean(PARAM_SHOW_PROGRESS, isShowProgress)
        progressBarHeight = arguments.getInt(PARAM_PROGRESS_HEIGHT, progressBarHeight)
        progressBarColor = arguments.getInt(PARAM_PROGRESS_COLOR, progressBarColor)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val layoutContainer = LinearLayout(context)
        layoutContainer.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        layoutContainer.orientation = LinearLayout.VERTICAL
        //添加TitleBar
        if (isShowTitle) {
            val actionBar = inflater.inflate(R.layout.layout_action_bar, layoutContainer, false)
            actionBar.setBackgroundColor(titleBarColor)
            actionBar.findViewById<ImageView>(R.id.action_back)
                .setOnClickListener { back() }
            mTvTitle = actionBar.findViewById(R.id.action_title)
            mTvTitle!!.text = requireArguments().getString(PARAM_WEB_TITLE, "")
            //设置分割线
            if (isShowDivider) {
                val divider = actionBar.findViewById<View>(R.id.action_divider)
                divider.setBackgroundColor(dividerColor)
                divider.visibility = if (isShowDivider) View.VISIBLE else View.GONE
            }
            layoutContainer.addView(actionBar)
        }
        //添加进度条
        if (isShowProgress) {
            mProgressBar = ProgressBar(
                requireContext(), null, android.R.attr.progressBarStyleHorizontal
            )
            mProgressBar!!.layoutParams =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, progressBarHeight)
            mProgressBar!!.progressDrawable = ClipDrawable(
                ColorDrawable(progressBarColor), Gravity.START, ClipDrawable.HORIZONTAL
            )
            mProgressBar!!.visibility = View.GONE
            layoutContainer.addView(mProgressBar)
        }
        mWeb = WebView(requireContext())
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
        params.weight = 1f
        mWeb.layoutParams = params
        layoutContainer.addView(mWeb)
        return layoutContainer
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments == null) {
            requireActivity().onBackPressed()
            return
        }

        //加载Url
        val url = requireArguments().getString(PARAM_WEB_URL)
        if (TextUtils.isEmpty(url)) {
            throw NullPointerException("web_url can not be null")
        }
        mWeb.loadUrl(url!!)
        var webClient = WebDelegate.instance.webClient
        if (webClient == null) {
            webClient = WebDelegate.WebClient()
        }
        webClient.applySettings(mWeb)
        mWeb.webViewClient = webClient

        var chromeClient = WebDelegate.instance.chromeClient
        if (chromeClient == null) {
            chromeClient = WebDelegate.ChromeClient()
        }
        mWeb.webChromeClient = chromeClient
    }

    private fun back() {
        if (mWeb.canGoBack()) {
            mWeb.goBack()
        } else {
            requireActivity().onBackPressed()
        }
    }
}


