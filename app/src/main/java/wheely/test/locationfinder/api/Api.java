package wheely.test.locationfinder.api;


import android.os.Handler;
import android.util.Log;

import java.io.IOException;

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

public class Api {

    private static final String TAG = "Api";
    private Handler eventQueue = new Handler();
    private WebSocket currentSocket;
    private long heartbeatTimeout = 20000;
    private long reconnectInterval = 10000;
    private long heartbeatInterval = 1000;

    private Runnable connect = this::createSocket;

    private Runnable ping = this::ping;

    private Runnable close = this::close;

    private String username;
    private String password;

    public void connect(String username, String password) {
        this.username = username;
        this.password = password;
        close();
        createSocket();
    }

    private void ping() {
        try {
            currentSocket.sendPing(null);
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            if(currentSocket != null)
                currentSocket.close(3001, "timeout");
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
        final String url = "ws://mini-mdt.wheely.com?username="
                + username + "&password=" + password;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        WebSocketCall.create(client, request).enqueue(new WebSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                currentSocket = webSocket;
                pingDelayed();

                try {
                    webSocket.sendMessage(new RequestBody() {
                        @Override
                        public MediaType contentType() {
                            return WebSocket.TEXT;
                        }

                        @Override
                        public void writeTo(BufferedSink sink) throws IOException {
                            sink.writeUtf8("{\n" +
                                    "                    \"lat\": 55.373703,\n" +
                                    "                    \"lon\": 37.474764\n" +
                                    "                }");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                close();
                createSocket(reconnectInterval);
            }

            @Override
            public void onMessage(ResponseBody message) throws IOException {
                Log.d(TAG, "message : " + message.string());
            }

            @Override public void onPong(Buffer payload) {
                Log.d(TAG, "PONG");
                pingDelayed();
            }
            @Override public void onClose(int code, String reason) {
                Log.d(TAG, "CLOSE: " + code + " " + reason);
                createSocket(reconnectInterval);
            }

        });
        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown();
    }
}
