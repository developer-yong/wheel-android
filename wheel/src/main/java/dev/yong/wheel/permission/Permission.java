package dev.yong.wheel.permission;

import android.os.Build;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author coderyong
 */
public class Permission {

    private static String TAG = "Permissions";

    public static Builder with(FragmentActivity activity) {
        return new Builder(activity);
    }

    public static class Builder {
        FragmentActivity activity;
        List<String> permissions;

        Builder(FragmentActivity activity) {
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

        private Fragment findPermissionsFragment(FragmentActivity activity) {
            Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new PermissionsFragment();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
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
