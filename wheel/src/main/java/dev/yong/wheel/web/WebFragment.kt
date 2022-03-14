package dev.yong.wheel.web

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import dev.yong.wheel.R
import dev.yong.wheel.permission.IPermissionResult
import dev.yong.wheel.permission.registerPermissionResult
import dev.yong.wheel.web.IFileChooserResult.FileChooserResult

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
 * WebContainer support_file_chooser 参数
 */
const val PARAM_SUPPORT_FILE_CHOOSER = "web_support_file_chooser"

/**
 * WebContainer handle_back_pressed 参数
 */
const val PARAM_HANDLE_BACK_PRESSED = "web_handle_back_pressed"

/**
 * @author coderyong
 */
open class WebFragment : Fragment(), IPermissionResult, IFileChooserResult {

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

    private var supportFileChooser = false
    private var handleOnBackPressed = true

    private var mPermissionResultLauncher: ActivityResultLauncher<Array<String>>? = null
    private var mFileResultLauncher: ActivityResultLauncher<FileChooserResult>? =
        null
    private var mFileChooserParams: FileChooserResult? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null

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
        //获取是否支持文件选择
        supportFileChooser = arguments.getBoolean(PARAM_SUPPORT_FILE_CHOOSER, supportFileChooser)
        //获取是否处理返回键事件
        handleOnBackPressed = arguments.getBoolean(PARAM_HANDLE_BACK_PRESSED, handleOnBackPressed)
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

        if (supportFileChooser) {
            val requireActivity = requireActivity()
            mPermissionResultLauncher = requireActivity.registerPermissionResult(this)
            mFileResultLauncher = requireActivity.registerFileChooserResult(this)
        }

        var chromeClient = WebDelegate.instance.chromeClient
        if (chromeClient == null) {
            chromeClient = object : WebDelegate.ChromeClient() {
                override fun onShowFileChooser(
                    webView: WebView,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    if (supportFileChooser) {
                        if (fileChooserParams != null) {
                            mFileChooserParams = FileChooserResult(
                                webView.context,
                                fileChooserParams
                            )
                            mFilePathCallback = filePathCallback
                            if (mFileChooserParams!!.isCaptureEnabled) {
                                //申请相机权限
                                if (mPermissionResultLauncher != null) {
                                    mPermissionResultLauncher!!.launch(
                                        arrayOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        )
                                    )
                                } else {
                                    filePathCallback?.onReceiveValue(null)
                                }
                            } else {
                                mFileResultLauncher?.launch(mFileChooserParams)
                            }
                        } else {
                            filePathCallback?.onReceiveValue(null)
                        }
                        return true
                    }
                    return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
                }
            }
        }
        mWeb.webChromeClient = chromeClient

        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(handleOnBackPressed) {
                override fun handleOnBackPressed() {
                    back()
                }
            })
    }

    open fun canGoBack(): Boolean {
        return mWeb.canGoBack()
    }

    open fun back() {
        if (mWeb.canGoBack()) {
            mWeb.goBack()
        } else {
            requireActivity().onBackPressed()
        }
    }

    override fun onPermissionResult(grantedResult: Map<String, Boolean>) {
        if (grantedResult[Manifest.permission.CAMERA]!! &&
            grantedResult[Manifest.permission.WRITE_EXTERNAL_STORAGE]!!
        ) {
            mFileResultLauncher?.launch(mFileChooserParams)
        } else {
            onChooserResult(null)
        }
    }

    override fun onChooserResult(uriResult: Array<Uri>?) {
        if (mFilePathCallback != null) {
            mFilePathCallback!!.onReceiveValue(uriResult)
            mFilePathCallback = null
        }
    }
}


