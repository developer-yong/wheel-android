package dev.yong.wheel.permission;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author coderyong
 */
public class Permission {

    private static String TAG = "Permissions";

    public static Builder with(Activity activity) {
        return new Builder(activity);
    }

    public static class Builder {
        Activity activity;
        List<String> permissions;

        Builder(Activity activity) {
            if (activity == null) {
                throw new IllegalStateException("Activity must not be null");
            }
            this.activity = activity;
        }

        public Builder check(String... permissions) {
            if (this.permissions == null) {
                this.permissions = new ArrayList<>();
            }
            if (permissions != null) {
                this.permissions.addAll(Arrays.asList(permissions));
            }
            return this;
        }

        public void request(PermissionGrantedListener listener) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (listener != null) {
                    TAG = listener.toString();
                    listener.onGranted(true);
                }
            } else {
                if (listener != null) {
                    TAG = listener.toString();
                }
                Fragment fragment = findPermissionsFragment(activity);
                if (fragment instanceof PermissionsFragment) {
                    ((PermissionsFragment) fragment).requestPermissions(listener, permissions);
                }
            }
        }

        private Fragment findPermissionsFragment(Activity activity) {
            Fragment fragment = activity.getFragmentManager().findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new PermissionsFragment();
                FragmentManager fragmentManager = activity.getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(fragment, TAG)
                        .commitAllowingStateLoss();
                fragmentManager.executePendingTransactions();
            }
            return fragment;
        }

    }

    public interface PermissionGrantedListener {
        /**
         * 权限授予回调
         *
         * @param granted 是否授予
         */
        void onGranted(boolean granted);
    }
}
