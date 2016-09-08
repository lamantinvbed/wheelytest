package wheely.test.locationfinder;


import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Api {

    private Socket socket;

    public void createSocket() {
        String ip = "http://mini-mdt.wheely.com";
        System.out.println("EMIT_DEBUG OnLine : IP " + ip);
        IO.Options opts = new IO.Options();

        opts.forceNew = true;
        opts.reconnection = true;
        opts.transports = new String[]{"websocket"};
        opts.query = "username=addd&password=afff";
        try {
            socket = IO.socket(ip, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if(socket != null) {
            openSocket();
        }
    }

    private void openSocket() {
        socket.off();
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("EMIT_DEBUG OnLine connected");

            }
        }).on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("EMIT_DEBUG OnLine connecting");

            }
        }).on(Socket.EVENT_PING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("EMIT_DEBUG OnLine ping");

            }
        }).on(Socket.EVENT_PONG, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("EMIT_DEBUG OnLine pong");

            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("EMIT_DEBUG OnLine EVENT_DISCONNECT");
                if (args != null && args.length > 0) {
                    String reason = args[0].toString();
                    System.out.println("EMIT_DEBUG disconnect error : " + reason);
                    if (reason.contains("transport error") || reason.contains("io server disconnect"))
                        System.out.println("disconnect from server");
                }
            }
        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("EMIT_DEBUG OnLine EVENT_CONNECT_ERROR ");
                if (args != null && args.length > 0) {
                    System.out.println("EMIT_DEBUG OnLine EVENT_CONNECT_ERROR " + args[0]);
//                    if ((args[0]).toString().contains("EngineIOException")) reconnect();
                }
            }
        }).on(Socket.EVENT_RECONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("EMIT_DEBUG OnLine EVENT_RECONNECT_ERROR");
                if (args != null && args.length > 0)
                    System.out.println("EMIT_DEBUG OnLine args = " + args[0]);
            }
        }).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("EMIT_DEBUG OnLine EVENT_RECONNECT");
            }
        }).on(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("EMIT_DEBUG OnLine EVENT_RECONNECT_ATTEMPT");
                if (args != null && args.length > 0)
                    System.out.println("EMIT_DEBUG OnLine args = " + args[0]);
            }
        }).on(Socket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("EMIT_DEBUG OnLine EVENT_RECONNECT_FAILED");
                if (args != null && args.length > 0)
                    System.out.println("EMIT_DEBUG OnLine args = " + args[0]);
            }
        }).on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("EMIT_DEBUG OnLine EVENT_RECONNECTING");
                if (args != null && args.length > 0)
                    System.out.println("EMIT_DEBUG OnLine args = " + args[0]);
            }
        }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("EMIT_DEBUG OnLine EVENT_CONNECT_TIMEOUT");
                if (args != null && args.length > 0)
                    System.out.println("EMIT_DEBUG OnLine args = " + args[0]);

            }
        }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("EMIT_DEBUG OnLine EVENT_ERROR " + args);
            }
        });
        socket.open();
    }
}
