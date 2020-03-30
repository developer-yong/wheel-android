package dev.yong.wheel.permission;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.List;

/**
 * @author coderyong
 */
public class PermissionsFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_CODE = 42;


    private Permission.PermissionGrantedListener mPermissionGrantedListener;

    public PermissionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestPermissions(Permission.PermissionGrantedListener listener, List<String> permissions) {
        mPermissionGrantedListener = listener;

        if (checkPermission(permissions) && mPermissionGrantedListener != null) {
            mPermissionGrantedListener.onGranted(true);
        } else {
            //请求权限弹窗
            requestPermissions(permissions.toArray(new String[]{}), PERMISSIONS_REQUEST_CODE);
        }
    }

    private boolean checkPermission(List<String> permissions) {
        boolean isGranted = getActivity() != null;

        if (getActivity() != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(
                        getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                }
            }
        }
        return isGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // If request is cancelled, the result arrays are empty.
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (mPermissionGrantedListener != null) {
                mPermissionGrantedListener.onGranted(
                        grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
            }
        } else {
            if (mPermissionGrantedListener != null) {
                mPermissionGrantedListener.onGranted(checkPermission(Arrays.asList(permissions)));
            }
        }
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().remove(this).commit();
            getFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPermissionGrantedListener = null;
    }
}