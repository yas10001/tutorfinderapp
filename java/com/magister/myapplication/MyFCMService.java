package com.magister.myapplication;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFCMService extends FirebaseMessagingService {

    private static final String TAG = "MyFCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Received FCM Notification: " + title + " - " + message);
        }
    }

    @Override
    public void onNewToken(String token) {
        // Handle token refresh
        Log.d(TAG, "Refreshed FCM Token: " + token);

        // You can send the refreshed token to your server if necessary
        // ...
    }

    public static void sendNotificationToTutor(String tutorEmail, NotificationPayload payload) {
        String title = payload.getTitle();
        String message = payload.getMessage();
        String startDate = payload.getStartDate() != null ? payload.getStartDate() : "No start date";

        // Send notification to the registering tutor
        sendNotification(tutorEmail, "Your tutor account is under review. It will be activated soon.");

        // Send notification to the tutor
        sendNotification(tutorEmail, "Notification to Tutor:\nTitle: " + title + "\nMessage: " + message + "\nStart Date: " + startDate);
    }

    private static void sendNotification(String tutorEmail, String message) {
        // Implement the logic to send the notification to the tutor's device
        // Use the provided tutorEmail and message to construct and send the notification
        // For example, using FCM send API
        // ...

        // Sample code to demonstrate sending the notification to logcat
        String notification = "Notification to Tutor:\nMessage: " + message;
        Log.d(TAG, notification);
    }
}
