package com.krosskomics.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.krosskomics.BuildConfig
import com.krosskomics.KJKomicsApp
import com.krosskomics.R
import com.krosskomics.splash.SplashActivity
import com.krosskomics.util.CommonUtil
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    private var sound: String? = ""
    private var vibration: String? = ""
    private var largeIcon: String? = ""
    var title: String? = ""
    var message: String? = ""
    var imgurl: String? = ""
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }
            remoteMessage.apply {
                title = data["title"]
                message = data["body"]
                KJKomicsApp.ATYPE = data["atype"]
                KJKomicsApp.SID = data["sid"]
                imgurl = data["image"]
                sound = data["sound"]
                vibration = data["vibration"]
                largeIcon = data["largeicon"]
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "KJKomicsApp.ATYPE : " + KJKomicsApp.ATYPE)
                }
                // type 분류
                sendNotification(title, message, imgurl)
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
//        // [START dispatch_job]
//        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
//        WorkManager.getInstance().beginWith(work).enqueue()
//        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(title: String?, message: String?, imgurl: String?) {
        var intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        if (TextUtils.isEmpty(KJKomicsApp.SID)) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        } else {
            if ("com.krosskomics.MainActivity" == CommonUtil.getRunningClass(this) || "com.krosskomics.page.WebtoonComicsActivity" == CommonUtil.getRunningClass(
                    this
                ) || "com.krosskomics.page.MoreActivity" == CommonUtil.getRunningClass(this) || "com.krosskomics.page.TopRankingActivity" == CommonUtil.getRunningClass(
                    this
                ) || "com.krosskomics.page.BookActivity" == CommonUtil.getRunningClass(this) || "com.krosskomics.page.ShowActivity" == CommonUtil.getRunningClass(
                    this
                ) || "com.krosskomics.page.GiftBoxActivity" == CommonUtil.getRunningClass(this) || "com.krosskomics.page.MyBookActivity" == CommonUtil.getRunningClass(
                    this
                ) || "com.krosskomics.page.CoinActivity" == CommonUtil.getRunningClass(this) || "com.krosskomics.page.SearchActivity" == CommonUtil.getRunningClass(
                    this
                )
            ) {
                intent = CommonUtil.setPushAction(this, KJKomicsApp.ATYPE, KJKomicsApp.SID)
                intent.putExtra("atype", KJKomicsApp.ATYPE)
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            } else {
                intent.putExtra("atype", KJKomicsApp.ATYPE)
                intent.putExtra("sid", KJKomicsApp.SID)
            }
            if (!TextUtils.isEmpty(KJKomicsApp.SID)) {
                pendingIntent = PendingIntent.getActivity(
                    this,
                    KJKomicsApp.SID!!.toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        }
        val channelId = getString(R.string.default_notification_channel_id)
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
        notificationBuilder
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setSmallIcon(getNotificationIcon())
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        if ("0" == sound && "0" == vibration) {
        } else if ("0" == sound && "1" == vibration) {
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
        } else if ("1" == sound && "0" == vibration) {
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND)
        } else if ("1" == sound && "1" == vibration) {
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
        }
        if ("1" == largeIcon) {
            notificationBuilder.setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.mipmap.ic_launcher
                )
            )
        }
        if (!TextUtils.isEmpty(imgurl)) {
            val bigPicture = getBitmapFromURL(imgurl)
            val bigStyle: NotificationCompat.BigPictureStyle =
                NotificationCompat.BigPictureStyle(notificationBuilder)
            bigStyle.setBigContentTitle(title)
            bigStyle.setSummaryText(message)
            bigStyle.bigPicture(bigPicture)
            notificationBuilder.setStyle(bigStyle)
            notificationBuilder.priority = Notification.PRIORITY_MAX
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.app_name2),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        if (TextUtils.isEmpty(KJKomicsApp.SID)) {
            notificationManager.notify(0, notificationBuilder.build())
        } else {
            notificationManager.notify(KJKomicsApp.SID!!.toInt(), notificationBuilder.build())
        }
    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(src)
            connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * 롤리팝이전과 이후의 아이콘 가져오기
     *
     * @return
     */
    private fun getNotificationIcon(): Int {
        val whiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (whiteIcon) R.mipmap.ic_launcher else R.mipmap.ic_launcher
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}