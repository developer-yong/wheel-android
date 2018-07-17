package dev.yong.wheel.permission;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

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
        boolean isGranted = true;

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(
                    getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            }
        }
        return isGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (mPermissionGrantedListener != null) {
                    mPermissionGrantedListener.onGranted(
                            grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                }
                break;
            }
            default:
                if (mPermissionGrantedListener != null) {
                    mPermissionGrantedListener.onGranted(checkPermission(Arrays.asList(permissions)));
                }
                break;
        }
    }
}