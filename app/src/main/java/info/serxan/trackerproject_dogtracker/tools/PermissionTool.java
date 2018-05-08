package info.serxan.trackerproject_dogtracker.tools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;

public class PermissionTool {

    /**
     * List all permissions we need.
     * We will use this array and call "requestPermissions"
     * to request permissions when the application is launched.
     *
     */
    public static final String[] PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET
    };

    /**
     * A request code.
     * It will be called in "requestPermissions" method.
     * You can use this request code and make a callback from it.
     *
     */
    public static final int PERMS_REQUEST = 1337;

    /**
     * Main activity context.
     * This context is used as permission checker.
     *
     */
    private Context context;

    /**
     * Main activity Activity context.
     * This context is used as permission requester.
     *
     */
    private AppCompatActivity activity;

    /**
     * This constructor will be called from the Main activity.
     *
     */
    public PermissionTool(Context context, AppCompatActivity activity) {
        this.context = context;
        this.activity = activity;
    }

    /**
     * This constructor will be called from the Tracker Service.
     *
     */
    public PermissionTool(Context context) {
        this.context = context;
    }

    /**
     * We ask all permissions we need from the "PERMS" array.
     *
     */
    public void ask() {
        this.activity.requestPermissions(PERMS, PERMS_REQUEST);
    }

    /**
     * We check if all the permissions we need are granted.
     *
     */
    public boolean check() {
        boolean permLocation = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        boolean permAccounts = hasPermission(Manifest.permission.GET_ACCOUNTS);
        boolean permPhone = hasPermission(Manifest.permission.READ_PHONE_STATE);
        boolean permInternet = hasPermission(Manifest.permission.INTERNET);

        return permLocation && permAccounts && permPhone && permInternet;
    }

    /**
     * Check if the permission is granted from a specific permission.
     *
     */
    public boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED == this.context.checkSelfPermission(perm));
    }

}
