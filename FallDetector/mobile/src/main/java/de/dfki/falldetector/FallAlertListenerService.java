package de.dfki.falldetector;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Set;

import de.dfki.falldetectorcommons.ShortContact;

/**
 * This listener receives instructions from the watch, namely fall alerts upon
 * which it sends SMS to the contacts saved by ContactSelector
 */
public class FallAlertListenerService extends WearableListenerService {

    private static final String SEND_SMS_PATH = "/send/FallAlertSms";
    private static final String TAG = "AlertListener";

    private BroadcastReceiver bcrec;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate called");
        if (bcrec == null) {
            bcrec = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Log.i(TAG, "sms sent");
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Log.e(TAG, "error sending sms");
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Log.e(TAG, "sms sending failed because service is currently unavailable");
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Log.e(TAG, "sms sending failed because radio was explicitly turned off");
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Log.e(TAG, "sms sending failed because no pdu provided");
                    }
                }
            };
            registerReceiver(bcrec, new IntentFilter("SENT_SMS"));
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        Log.d(TAG, "peer connected: " + peer.getDisplayName());
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        Log.d(TAG, "peer disconnected: " + peer.getDisplayName());
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "got message with path: " + messageEvent.getPath());
        if (messageEvent.getPath().equals(SEND_SMS_PATH)) {
            Log.d(TAG, "Alert! sending SMS");
            new Thread(new smsSender(this)).start();
        }
    }

    private class smsSender implements Runnable {

        private Context context;
        private Location location;

        public smsSender(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            // Acquire a reference to the system Location Manager
            final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Define a listener that responds to location updates
            final LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    location = location;
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 50, locationListener);

            // Start 5min countdown for SMS
            new CountDownTimer(300000, 160000) {

                @Override
                public void onTick(long l) {

                }

                //Send sms with location fix after five minutes
                @Override
                public void onFinish() {
                    SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    Set<String> contacts = prefs.getStringSet(getString(R.string.contacts_key), null);
                    String[] numbers = new String[contacts.size()];
                    int i = 0;
                    for (String c : contacts) {
                        numbers[i] = ShortContact.parseNumber(c);
                        i++;
                    }

                    if (numbers != null && numbers.length > 0) {
                        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(
                                "SENT_SMS").setClass(context, FallAlertListenerService.class), 0);
                        SmsManager sm = SmsManager.getDefault();
                        String message = getString(R.string.sms_message);
                        //stop getting gps updates
                        locationManager.removeUpdates(locationListener);
                        if (location != null) {
                            message = message + "Coordinates: " + Location.convert(location.getLatitude(), Location.FORMAT_DEGREES) + " " + Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
                        }
                        Log.d(TAG, "sending SMS to " + numbers.length + " numbers");
                        for (String n : numbers) {
                            if (n != null) {
                                sm.sendTextMessage(n, null, message, null, null);
                            } else {
                                Log.e(TAG, "null number in contacts");
                            }
                        }
                        Log.d(TAG, "SMS sent");
                    } else {
                        Log.e(TAG, "no numbers selected");
                    }
                }
            }.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bcrec);
    }
}
