package wheely.test.locationfinder.model.api;


import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketCall;
import okhttp3.ws.WebSocketListener;
import okio.Buffer;
import okio.BufferedSink;
import rx.Observable;
import rx.subjects.PublishSubject;
import wheely.test.locationfinder.model.dto.ApiLocation;
import wheely.test.locationfinder.model.dto.UserLocation;

public class Api {

    private static final String TAG = "Api";
    public static final String TIMEOUT = "timeout";
    public static final int TIMEOUT_CODE = 3001;
    private static final int FORCE_CLOSE_CODE = 3002;
    private static final String FORCE_CLOSE = "force_close";
    private Handler eventQueue = new Handler();
    private WebSocket currentSocket;
    private long heartbeatTimeout = 20000;
    private long reconnectInterval = 10000;
    private long heartbeatInterval = 1000;

    private Runnable connect = this::createSocket;

    private Runnable ping = this::ping;

    private Runnable close = this::closeWithTimeout;

    private boolean isConnected;
    private String username;
    private String password;
    private UserLocation userLocation;

    private final PublishSubject<List<ApiLocation>> subject = PublishSubject.create();

    public void connect(String username, String password) {
        eventQueue.removeCallbacksAndMessages(null);
        this.username = username;
        this.password = password;
        createSocket();
    }

    public void setLocation(UserLocation userLocation) {
        this.userLocation = userLocation;
        if(isConnected) {
            forceClose();
            connect(username, password);
        }
    }

    public Observable<List<ApiLocation>> getLocations() {
        return subject;
    }

    private void ping() {
        try {
            currentSocket.sendPing(null);
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void forceClose() {
        Log.d(TAG, "forceClose()");
        close(FORCE_CLOSE_CODE, FORCE_CLOSE);
    }

    private void closeWithTimeout() {
        Log.d(TAG, "closeWithTimeout()");
        close(TIMEOUT_CODE, TIMEOUT);
    }

    private void close(int code, String reason) {
        try {
            if(currentSocket != null)
                currentSocket.close(code, reason);
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void pingDelayed() {
        eventQueue.removeCallbacks(ping);
        eventQueue.removeCallbacks(close);
        eventQueue.postDelayed(ping, heartbeatInterval);
        eventQueue.postDelayed(close, heartbeatTimeout);
    }

    private void createSocket(long reconnectInterval) {
        eventQueue.removeCallbacksAndMessages(null);
        eventQueue.postDelayed(connect, reconnectInterval);
    }

    private void createSocket() {
        final String url = Constants.BASE_URL + "?username=" + username + "&password=" + password;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        WebSocketCall.create(client, request).enqueue(new WebSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                currentSocket = webSocket;
                pingDelayed();
                isConnected = true;
                sendLocation();
            }

            @Override
            public void onFailure(IOException e, Response response) {
                Log.d(TAG, "onFailure() exception: " + e);
                if(e != null) {
                    e.printStackTrace();
                }
                if(response != null) {
                    try {
                        Log.d(TAG, "onFailure : message: " + response.message()
                                + " body: " + response.body().string());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                isConnected = false;
                forceClose();
                createSocket(reconnectInterval);
            }

            @Override
            public void onMessage(ResponseBody message) throws IOException {
                String messageStr = message.string();
                subject.onNext(ApiLocation.getLocationsListFromJson(messageStr));
                Log.d(TAG, "message : " + messageStr);

            }

            @Override public void onPong(Buffer payload) {
                Log.d(TAG, "PONG");
                pingDelayed();
            }
            @Override public void onClose(int code, String reason) {
                isConnected = false;
                Log.d(TAG, "CLOSE: " + code + " " + reason);
                if(code == TIMEOUT_CODE && reason.equals(TIMEOUT)) {
                    createSocket(reconnectInterval);
                }

            }

        });
        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown();
    }

    private void sendLocation() {
        if(userLocation != null && isConnected) {
            try {
                currentSocket.sendMessage(new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return WebSocket.TEXT;
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {
                        Log.d(TAG, "sending location : " + userLocation.toString());
                        sink.writeUtf8(userLocation.toJson().toString());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
