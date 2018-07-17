package dev.yong.wheel.base.mvp;


/**
 * @author coderyong
 */
public interface IModel {

    /**
     * Model处理释放
     */
    void onDestroy();

    interface ModelCallBack<T> {
        /**
         * 用于数据层数据获取成功后回调
         *
         * @param t 得到的数据
         */
        void onSuccess(T t);

        /**
         * 获取失败
         *
         * @param errorMessage 错误信息
         */
        void onFail(String errorMessage);
    }
}
