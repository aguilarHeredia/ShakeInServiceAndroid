package guy.droid.com.shakeonservice;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.squareup.seismic.ShakeDetector;

import java.util.TimerTask;

/**
 * Created by admin on 8/29/2016.
 */
public class MyService extends Service implements ShakeDetector.Listener{
    LocationManager locationManager;
    Boolean isGPSEnabled,isNetworkProviderEnabled;
    public static final long NOTIFY_INTERVAL = 3 * 1000; // 10 seconds
    android.os.Handler handler = new android.os.Handler();
    java.util.Timer timer = null;
    Vibrator v;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(timer!=null)
        {
            timer.cancel();
        }
        else
        {
            timer = new java.util.Timer();
        }
        timer.scheduleAtFixedRate(new Timer(),0,NOTIFY_INTERVAL);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(),"SERVICE STARTED",Toast.LENGTH_SHORT).show();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);
        try{

      /*  handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"RUNNIG",Toast.LENGTH_SHORT).show();
            }
        },5000);*/
           /* handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"RUNNIG",Toast.LENGTH_SHORT).show();
                }
            });*/


        }catch (Exception e)
        {
            Log.w("RAZ","  "+e);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"SERVICE STOPPPED",Toast.LENGTH_SHORT).show();
        timer.cancel();
    }

    @Override
    public void hearShake() {

        try {

            if (getLastBestLocation() == null) {

            } else {
                // getLastBestLocation();
                String lat = getLastBestLocation().getLatitude() + "";
                String longs = getLastBestLocation().getLongitude() + "";
                Toast.makeText(getApplicationContext(),"LAT : "+lat+" LONG :"+longs,Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e)
        {

        }
        v.vibrate(500);
    }

    private Location getLastBestLocation()  throws  Exception{
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(isGPSEnabled == false && isNetworkProviderEnabled == false)
        {
            Toast.makeText(getApplicationContext(),"Turn On Your GPS",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                Toast.makeText(getApplicationContext(),"Please Enable App Permissions",Toast.LENGTH_LONG).show();
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getApplication().getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    //If the user has denied the permission previously your code will come to this block
                    //Here you can explain why you need this permission
                    //Explain here why you need this permission
                }
                ActivityCompat.requestPermissions((Activity) getApplication().getApplicationContext(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},23);


            }else
            {
                //  Toast.makeText(getApplicationContext(),"Turn On Your GPS",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

            //  return null;
        }



        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }

    }

    class Timer extends TimerTask {

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                  //  Toast.makeText(getApplicationContext(),"RUNNING",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
