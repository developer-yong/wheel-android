package dev.yong.wheel.web;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author coderyong
 */
public class JSBridgeHelper {

    private JSBridgeHelper() {
    }

    private static class JSBridgeHelperHolder {
        private final static JSBridgeHelper INSTANCE = new JSBridgeHelper();
    }

    public static JSBridgeHelper getInstance() {
        return JSBridgeHelperHolder.INSTANCE;
    }

    private List<JSBridge> mBridges = null;

    /**
     * 添加JSBridge实现
     *
     * @param bridge 继承至{@link JSBridge}并实现JS调用函数
     * @see WebView#addJavascriptInterface(Object, String)
     */
    public JSBridgeHelper addJavascriptInterface(JSBridge bridge) {
        if (mBridges == null) {
            mBridges = new ArrayList<>();
        }
        mBridges.add(bridge);
        return this;
    }

    /**
     * 移除JSBridge实现
     *
     * @param bridge 继承至{@link JSBridge}
     * @see WebView#removeJavascriptInterface(String)
     */
    public JSBridgeHelper removeJavascriptInterface(JSBridge bridge) {
        if (mBridges != null) {
            if (bridge != null) {
                bridge.detach();
            }
            mBridges.remove(bridge);
        }
        return this;
    }

    public JSBridgeHelper clear() {
        if (mBridges != null) {
            destroy();
            mBridges.clear();
        }
        return this;
    }

    public List<JSBridge> getJavascriptInterfaces() {
        return mBridges;
    }

    /**
     * 绑定JSBridge到webView
     *
     * @param webView WebView
     */
    public static void attach(WebView webView) {
        List<JSBridge> bridges = getInstance().mBridges;
        if (bridges != null) {
            for (JSBridge bridge : bridges) {
                bridge.attach(webView);
            }
        }
    }

    /**
     * 释放JSBridge
     */
    public static void destroy() {
        List<JSBridge> bridges = getInstance().mBridges;
        if (bridges != null) {
            for (JSBridge bridge : bridges) {
                bridge.destroy();
            }
        }
    }

    public static class JSBridge {

        protected final String mNamespaces;
        protected WebView mWebView;

        public JSBridge(String namespaces) {
            if (TextUtils.isEmpty(namespaces)) {
                throw new NullPointerException("JSBridge namespaces can not be null");
            }
            this.mNamespaces = namespaces;
        }

        public String getNamespaces() {
            return mNamespaces;
        }

        @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface"})
        private void attach(WebView webView) {
            this.mWebView = webView;
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.addJavascriptInterface(this, mNamespaces);
        }

        private void detach() {
            if (mWebView != null) {
                mWebView.removeJavascriptInterface(mNamespaces);
            }
        }

        private void destroy() {
            if (mWebView != null) {
                mWebView.removeJavascriptInterface(mNamespaces);
                mWebView = null;
            }
        }
    }
}
