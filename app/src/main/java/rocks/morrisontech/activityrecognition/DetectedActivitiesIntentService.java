package rocks.morrisontech.activityrecognition;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;

import java.util.ArrayList;

/**
 * Created by Craig Morrison on 8/8/16.
 */
public class DetectedActivitiesIntentService extends IntentService {
    private static String TAG = "detection_is";

    public DetectedActivitiesIntentService() {
        super(TAG); // use the tag to name the worker thread
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult activityRecognitionResult = ActivityRecognitionResult.extractResult(intent);
        ArrayList detectedActivities = (ArrayList) activityRecognitionResult.getProbableActivities();
        Intent detectedActivitiesBroadcast = new Intent(Constants.BROADCAST_ACTION);
        detectedActivitiesBroadcast.putExtra(Constants.ACTIVITY_RECOGNITION_EXTRA, detectedActivities);

        LocalBroadcastManager.getInstance(this).sendBroadcast(detectedActivitiesBroadcast);
        // get instance of LocalBroadcastManager to sendBroadcast with the Intent

        Log.i(TAG, "activities detected"); // always log when something is done!
    }

}
