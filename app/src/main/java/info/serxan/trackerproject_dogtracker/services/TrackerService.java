package info.serxan.trackerproject_dogtracker.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import info.serxan.trackerproject_dogtracker.tools.PermissionTool;

public class TrackerService extends Service {

    /**
     * The interval time in ms between each tracking request.
     *
     */
    public int configIntervalTime = 10000;
    /**
     *  The Firebase path where we will store all data.
     *
     */
    public String configFbPath = "positions";
    /**
     * PermissionTool class.
     */
    public PermissionTool permTool;

    /**
     * Phone latitude.
     */
    public double latitude;
    /**
     * Phone longitude.
     */
    public double longitude;
    /**
     * Phone Id.
     */
    public String phoneId;
    /**
     * Phone Brand.
     */
    public String phoneBrand;
    /**
     * Phone Model.
     */
    public String phoneModel;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * We instantiate the PermissionTool class with application context.
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();
        this.permTool = new PermissionTool(getApplication());
    }

    /**
     * When the service is started we call the method "sendData"
     * and start collecting the phone data in background thread.
     *
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.sendData();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Return the current data in a specific format.
     * Ex : "2018/04/02 22:22:25"
     *
     */
    public String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * We first check if the required permissions are granted.
     * Then we retrieve the phone coordinates.
     *
     */
    @SuppressLint("MissingPermission")
    public boolean setPosition() {
        if (!this.permTool.check()) {
            return false;
        }
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // --> GPS PROVIDERS : passive, gps, network, local_database.

        Location location = lm.getLastKnownLocation("passive");

        if (location == null) {
            return false;
        } else {
            this.longitude = location.getLongitude();
            this.latitude = location.getLatitude();
            return true;
        }
    }

    /**
     * setUserValues
     * @return
     */
    @SuppressLint({"MissingPermission", "NewApi"})
    public boolean setUserValues() {
        if (! this.permTool.check()) {
            return false;
        }

        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        this.phoneBrand = Build.BRAND;
        this.phoneModel = Build.MODEL;
        this.phoneId = tm.getImei();
        return true;
    }

    /**
     * sendData
     */
    public void sendData() {
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                boolean resPosition = setPosition();
                boolean respPhone = setUserValues();

                if (resPosition && respPhone) {
                    writeInDatabase();
                }

                handler.postDelayed(this, configIntervalTime);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    /**
     * writeInDatabase
     */
    public void writeInDatabase() {
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("longitude", this.longitude);
        data.put("latitude", this.latitude);
        data.put("phoneBrand", this.phoneBrand);
        data.put("phoneModel", this.phoneModel);
        data.put("phoneId", this.phoneId);
        data.put("createdAt", this.getCurrentDate());

        db.collection(this.configFbPath)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        System.out.println("DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error adding document");
                    }
                });
    }

}
