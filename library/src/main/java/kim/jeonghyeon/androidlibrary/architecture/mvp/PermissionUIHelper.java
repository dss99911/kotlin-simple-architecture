package kim.jeonghyeon.androidlibrary.architecture.mvp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import kim.jeonghyeon.androidlibrary.BaseApplication;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class PermissionUIHelper {
    private static int REQUEST_PERMISSION_SETTING = 65535;//this is max number for request code
    @NonNull
    private final WeakReference<Activity> uiReference;
    private final SparseArray<PermissionResultListener> mPermissionResultListeners = new SparseArray<>();
    private final AtomicInteger permissionRequestCode = new AtomicInteger(1);
    private Runnable permissionSettingListener;

    public PermissionUIHelper(Activity ui) {
        this.uiReference = new WeakReference<>(ui);
    }

    public void requestPermissions(String[] permissions, PermissionResultListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            listener.onPermissionGranted();
            return;
        }

        Activity ui = uiReference.get();
        if (ui == null) {
            return;
        }

        if (permissions == null || permissions.length == 0) {
            return;
        }

        int requestCode = permissionRequestCode.getAndIncrement();
        if (listener != null) {
            mPermissionResultListeners.put(requestCode, listener);
        }

        ActivityCompat.requestPermissions(ui, permissions, requestCode);
    }

    public void startPermissionSettingsPage(Runnable listener) {
        Activity ui = uiReference.get();
        if (ui == null) {
            return;
        }

        permissionSettingListener = listener;

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + ui.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ui.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Activity ui = this.uiReference.get();
        if (ui == null) {
            return;
        }

        final ArrayList<String> deniedPermissions = new ArrayList<>();
        boolean hasPermanentDenied = false;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                continue;
            }

            deniedPermissions.add(permissions[i]);
            boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(ui, permissions[i]);
            if (!shouldShow) {
                hasPermanentDenied = true;
            }
        }

        PermissionResultListener permissionResultListener = mPermissionResultListeners.get(requestCode);
        if (permissionResultListener == null) {
            return;
        }

        if (deniedPermissions.size() == 0) {
            try {
                permissionResultListener.onPermissionGranted();
            } catch (SecurityException ex) {
                permissionResultListener.onPermissionException();
            }
        } else if (hasPermanentDenied) {
            permissionResultListener.onPermissionDeniedPermanently((String[]) Objects.requireNonNull(deniedPermissions.toArray()));
        } else {
            permissionResultListener.onPermissionDenied((String[]) Objects.requireNonNull(deniedPermissions.toArray()));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (permissionSettingListener != null) {
                permissionSettingListener.run();
                permissionSettingListener = null;
            }
        }
    }
}