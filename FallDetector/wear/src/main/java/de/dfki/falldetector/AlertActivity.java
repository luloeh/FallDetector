package de.dfki.falldetector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * This class shows the alert screen where the user can cancel for 300 seconds.
 * After the countdown finishes, the paired phone is instructed to send emergency
 * SMS
 */
public class AlertActivity extends Activity {

    private TextView mTextView;
    private TextView mCountDown;
    private TextView closeInfo;
    private DismissOverlayView mDismissOverlay;
    private GestureDetector mDetector;
    private PowerManager.WakeLock mWakeLock;
    private Vibrator v;
    private int countDownSeconds = 300;
    private static final String TAG = "FDCountdown";
    private static final String SEND_SMS_PATH = "/send/FallAlertSms";
    private GoogleApiClient mGoogleApiClient;
    private boolean retrying = true;
    private long delay = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.buildGoogleApiClient();
        mGoogleApiClient.connect();
        if (mWakeLock == null || !mWakeLock.isHeld()) {
            //wake the screen up
            mWakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Alert");
            mWakeLock.acquire();
        }
        //inflate layout for round or square screen
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                closeInfo = (TextView) stub.findViewById(R.id.dismissInfo);
                mTextView = (TextView) stub.findViewById(R.id.SMStext);
                mCountDown = (TextView) stub.findViewById(R.id.counter);
                mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
                mDismissOverlay.setIntroText(R.string.long_press_intro);
                mDismissOverlay.showIntroIfNecessary();
                Log.d(TAG, "starting call countdown");
                //start countdown to send SMS
                new CountDownTimer(countDownSeconds * 1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        mCountDown.setText((millisUntilFinished / 1000) + "s");
                    }

                    public void onFinish() {
                        //stop vibrating when countdown finished
                        if (v.hasVibrator()) {
                            v.cancel();
                        }
                        //launch a new thread to alert phone
                        new Thread(new sendMessageToMobileTask()).start();
                    }
                }.start();
            }
        });
        //show cancel button on long press
        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent ev) {
                mDismissOverlay.show();
            }
        });
        //vibrate during countdown
        v = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        if (v.hasVibrator()) {
            Log.d("FDCountdown", "Vibrating");
            // pause for 1.5 seconds, vibrate for 1.5 seconds, repeat
            long[] pattern = {1500, 1500, 1500, 1500};
            v.vibrate(pattern, 0);
        }
    }

    /**
     * A runnable to asynchronously try to send an alert message to the phone,
     * will attempt to send until successful
     */
    private class sendMessageToMobileTask implements Runnable {

        @Override
        public void run() {
            // repeat until message went out
            while (retrying) {
                try {
                    Thread.sleep(delay); //wait a bit for things to change
                } catch (InterruptedException e) {
                    //just carry on earlier than expected
                }
                Log.d(TAG, "sending message to phone");
                if (mGoogleApiClient.isConnected()) {
                    NodeApi.GetConnectedNodesResult nodes =
                            Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                    if (nodes == null || nodes.getNodes() == null || nodes.getNodes().size() < 1) {
                        Log.e(TAG, "could not get any connected node");
                        //increase retry delay
                        delay *= 1.5;
                        continue;
                    }
                    Log.d(TAG, "sending message to node " + nodes.getNodes().get(0).getDisplayName());
                    Wearable.MessageApi.sendMessage(
                            mGoogleApiClient, nodes.getNodes().get(0).getId(), SEND_SMS_PATH, new byte[0]).setResultCallback(
                            new ResultCallback<MessageApi.SendMessageResult>() {
                                @Override
                                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                    if (sendMessageResult.getStatus().isSuccess()) {
                                        mTextView.setText("SMS sent");
                                        closeInfo.setText("long press to close");
                                        ((LinearLayout) mCountDown.getParent()).removeView(mCountDown);
                                        retrying = false;
                                    } else {
                                        Log.e(TAG, "Failed to send message with status code: "
                                                + sendMessageResult.getStatus().getStatusCode());
                                        delay *= 1.5;
                                    }
                                }
                            }
                    );
                } else {
                    Log.d(TAG, "googleApiClient not yet(?) connected");
                    delay *= 1.5;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    /**
     * This method tries to establish a connection to the Google Data Layer API
     * in order to communicate with the paired Smartphone
     */
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now we can use the Data Layer API to send messages to the phone
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
                            //reconnect if we are not connected or connecting
                            mGoogleApiClient.reconnect();
                        }
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                        //try again
                        buildGoogleApiClient();
                    }
                })
                        // Request access only to the Wearable API
                .addApi(Wearable.API)
                .build();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        if (v.hasVibrator()) {
            v.cancel();
        }
        Intent i = new Intent(this, FallDetectionService.class);
        this.startService(i);
        Log.i(TAG, "restarted fall detection service after alert activity stopped");
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }
}
