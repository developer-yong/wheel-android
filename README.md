如果没有MD编辑器可使用在线编辑器将内容复制到编辑器内查看【https://md.mzr.me/】

# wheel说明

### 包结构
- base->基类包
- cache->缓存文件管理类包
- download->下载管理类包
- http->网络请求帮助类包
- network->网络状态帮助类型包
- permission->权限帮助类包
- swipeback->滑动返回帮助类包
- utils->工具类包
- view->基础自定义View包
- web->Web管理类包

### 核心类使用
**Router【路由管理类】**

    //路由初始化，如果不需要拦截处理，拦截器可以为空
	Router.init(application, new Router.RouterInterceptor() {
            @Override
            public boolean intercept(String pagePath) {
                //pagePath 为路由路径
				
				//拦截操作，例如，路径以http开头启动Web页面
				
				//返回true拦截后续处理，false不拦截后续操作
                return false;
            }
	});

	//注册Activity页面路由
	Router.registerActivityPage(pagePath, Activity.class);
	//注册Fragment页面路由
	Router.registerActivityPage(pagePath, Fragment.class);
	
	//路由跳转
	Router.with(context).putExtra(PARAM_KEY, value).open(pagePath)

**JSBridge【JS交互类】**

    //JSBridge初始化
	JSBridgeHelper.getInstance().addJavascriptInterface(new CRMBridge());

	//JSBridge实现
	public class CRMBridge extends JSBridgeHelper.JSBridge {

			public CRMBridge() {
				//${BRIDGE_NAMESPACE}需要与前端调用定义一致
				//window.${BRIDGE_NAMESPACE}.onJsMessage("JS调用Native成功！")
				super(BRIDGE_NAMESPACE);
			}

			@JavascriptInterface
			public void onJsMessage(String message) {
				//接收到前端消息并解析处理

				//发送消息到前端
				mWebView.evaluateJavascript(
						"javascript:receiveMessage('" + message + "')", null);
			}
	}

**OkHttpHelper【网络请求帮助类】**

    //调用该方法可获取默认OkHttp构建对象进行重新构建
	OkHttpClient.Builder builder = OkHttpHelper.okHttp();

	//GET请求使用
	OkHttpHelper.get(url)
			.addQueryParameter(PARAMETER_KEY, parameterValue)
			.enqueue(new Callback<User>() {
					@Override
					public void onResponse(User user) {
							//请求成功
					}

					@Override
					public void onFailure(Throwable e) {
							//请求失败处理
					}
			});
			
	//POST请求使用
	OkHttpHelper.post(url)
			.setMediaType(mediaType)    //请求类型application/json等
			.addQueryParameter(PARAMETER_KEY, parameterValue)  //链接参数
			.add(PARAMETER_KEY, parameterValue)     //表单参数
			.setJson(jsonParameter)       //json参数，该方法会将请求类型自动设置为application/json
			.enqueue(new Callback<User>() {
					@Override
					public void onResponse(User user) {
							//请求成功
					}

					@Override
					public void onFailure(Throwable e) {
							//请求失败
					}
			});
					
	//upload请求使用
	OkHttpHelper.upload(url)
			.addFormDataParts(mediaType,partName,filePaths) 
			.addRequestProgressListener(new ProgressInterceptor.ProgressListener() {
					@Override
					public void onProgress(long currentLength, long totalLength, boolean done) {
							//请求进度处理，非必须
					}
			})
			.addResponseProgressListener(new ProgressInterceptor.ProgressListener() {
					@Override
					public void onProgress(long currentLength, long totalLength, boolean done) {
							//响应进度处理，非必须
					}
			})
			.enqueue(new Callback<String>() {
					@Override
					public void onResponse(String user) {
							//请求成功
					}

					@Override
					public void onFailure(Throwable e) {
							//请求失败
					}
			});

**MVP开发模式实现**

	class MainActivity : ViewBindActivity<LayoutContainerBinding>(), IView<MainPresenter> {

			private lateinit var mPresenter: MainPresenter

			override fun onCreate(savedInstanceState: Bundle?) {
					super.onCreate(savedInstanceState)

					//注册Mvp开发模式
					registerMvp(this)

					//Presenter业务处理方法调用
					mPresenter.xxxMethod()
			}

			override fun attachPresenter(presenter: MainPresenter) {
					mPresenter = presenter
			}
	}

**MVVM开发模式实现**

	class MainActivity : ViewBindActivity<LayoutContainerBinding>() {

			override fun onCreate(savedInstanceState: Bundle?) {
					super.onCreate(savedInstanceState)
					//获取Model实现对象
					val model = getModel(MainModel::class.java)
					//订阅数据更新
					model.getData().observe(this) {
							//当前数据已更新，it为当前数据
							val data = it
					}
			}
	}

