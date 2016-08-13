package rocks.morrisontech.activityrecognition;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;

import java.util.ArrayList;

/**
 * Created by Craig Morrison on 8/8/16.
 * This class is called from MainActivity in a class that returns a PendingIntent
 */
public class DetectedActivitiesIntentService extends IntentService {
    private static String TAG = "detection_is";

    public DetectedActivitiesIntentService() {
        super(TAG); // use the tag to name the worker thread
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // get activities from system and save them into an array list
        ActivityRecognitionResult activityRecognitionResult = ActivityRecognitionResult.extractResult(intent);
        ArrayList detectedActivities = (ArrayList) activityRecognitionResult.getProbableActivities();
        // create an intent to broadcast detectedActivities ArrayList to a listener
        // name the intent something specific as that name will be used in the listener so it knows which intent to listen for
        Intent detectedActivitiesBroadcast = new Intent(Constants.BROADCAST_ACTION);
        detectedActivitiesBroadcast.putExtra(Constants.ACTIVITY_RECOGNITION_EXTRA, detectedActivities);

        // use this to broadcast the intent to the listeners in the app
        LocalBroadcastManager.getInstance(this).sendBroadcast(detectedActivitiesBroadcast);

        Log.i(TAG, "activities sent to listeners"); // always log when something is done!
    }

}
