package rocks.morrisontech.activityrecognition;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        ResultCallback<Status> {


    private static final String TAG = "ACTIVITY_RECOGNITION";
    protected GoogleApiClient mGoogleApiClient;
    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    protected TextView mStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusText = (TextView) findViewById(R.id.detected_activities_text);
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        buildGoogleApiClient();
    }

    public void requestActivityUpdatesButtonHandler(View view) {
        if(!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "API Client not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                100,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);

    }

    public void removeActivityUpdatesButtonHandler(View view) {
        if(!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "API Client not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    public PendingIntent getActivityDetectionPendingIntent() {
        Log.i("pending intent", "got pending intent");
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
        Log.i("buildGoogleApiClient", "executed");
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.i("API connection result", "Successfully added activity detection API");
        }
        else
            Log.e("API Connection result", "Could not add activity detection API" + status.getStatusMessage());

    }

    public void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
        Log.i("onStart", "onStart executed");

    }

    public void onStop() {
        super.onStop();

        mGoogleApiClient.disconnect();
    }

    public void onResume() {

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));

        super.onResume();
        Log.i("onResume", "onResume executed");
    }

    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        Log.i("onPause", "onPause Executed");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "onConnected");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

        Log.i(TAG, "onConnectionSuspended");
    }



    public String getActivityString(int detectedActivityType) {
        Resources resources = this.getResources();
        Log.i("getActivityString", "getting strings...");
        switch (detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unknown);
        }

    }

    /**
     * receives any Intent Broadcasts with onReceive method
     */
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("onReceive", "broadcast receiver working");

            // this ArrayList will store the detectedActivities determined in DetectedActivitiesIntentService
            ArrayList<DetectedActivity> detectedActivities =
                    intent.getParcelableArrayListExtra(Constants.ACTIVITY_RECOGNITION_EXTRA);

            String strStatus = "";
            for (DetectedActivity thisActivity: detectedActivities) {
                strStatus += getActivityString(thisActivity.getType()) + ": " + thisActivity.getConfidence() + "%\n";
                mStatusText.setText(strStatus);
            }
        }
    }

}
