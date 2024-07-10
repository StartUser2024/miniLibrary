package org.university.minilibrary;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Set;
import java.util.Scanner;
import java.io.*;
import java.net.*;

public class MiniLibraryApplication {
    private static Selector selector = null;
    private static final Logger logger = LoggerFactory.getLogger(MiniLibraryApplication.class);

    public static void main(String args[]) {
        try {
            // Этап подготовки
            Scanner in = new Scanner(System.in);
            System.out.print("Input IP: ");
            //String ip = in.nextLine();
            String ip = "127.0.0.1";
            System.out.print("Input port (default 808): ");
            int port = 808;
            //port = in.nextInt();
            System.out.print("Input directory: ");
            //in.nextLine();
            //String directory = in.nextLine();
            String directory = "C:\\Users\\User\\Downloads\\Arhiv";
            System.out.printf("IP: %s  Port: %d  Directory: %s \n", ip, port, directory);
            in.close();

            // Открытие селектора и привязка серверного сокета
            selector = Selector.open();
            ServerSocketChannel serverSocketChannel
                    = ServerSocketChannel.open();
            ServerSocket serverSocket
                    = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            serverSocketChannel.configureBlocking(false);
            int ops = serverSocketChannel.validOps();
            serverSocketChannel.register(selector, ops, null);

            // Обработка подключений
            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys
                        = selector.selectedKeys();
                Iterator<SelectionKey> i
                        = selectedKeys.iterator();

                while (i.hasNext()) {
                    SelectionKey key = i.next();
                    if (key.isAcceptable()) {
                        // New client has been  accepted
                        handleAccept(serverSocketChannel,
                                key);

                    } else if (key.isReadable()) {
                        // We can run non-blocking operation
                        // READ on our client
                        handleRead(key, directory);
                    }
                    i.remove();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleAccept(ServerSocketChannel mySocket, SelectionKey key) throws IOException {

        System.out.println("Connection Accepted..");

        // Accept the connection and set non-blocking mode
        SocketChannel client = mySocket.accept();
        client.configureBlocking(false);

        // Register that client is reading this channel
        client.register(selector, SelectionKey.OP_READ);

    }

    @SneakyThrows
    private static void handleRead(SelectionKey key, String directory) throws IOException {
        System.out.println("Reading client's message.");
        long startTime = System.currentTimeMillis();

        // create a ServerSocketChannel to read the request
        SocketChannel client = (SocketChannel)key.channel();
        // Create ByteBuffer to read data
        ByteBuffer Buffer = ByteBuffer.allocate(1024*1024*5);
        client.read(Buffer);
        Buffer.flip();
        byte[] bytes = new byte[Buffer.remaining()];
        Buffer.get(bytes);

        String json = new String(bytes);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        String filename = jsonObject.get("name").getAsString();
        long length = jsonObject.get("length").getAsLong();
        byte[] payload = gson.fromJson(jsonObject.get("data"), byte[].class);

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        double speed = length / (elapsedTime / 1000.0);
        System.out.println("Speed: " + speed + " bytes/sec");

        System.out.println("FileName: " + filename);
        System.out.println("Length: " + length);
        System.out.println("Payload: " + new String(payload));

        FileOutputStream fos = new FileOutputStream(directory + "\\" + filename);
        fos.write(payload);
        fos.close();
        System.out.println("Received message - fileName: " + filename + ", length: " + length + ", payload: " + new String(payload));
        System.out.println("File received: " + filename);

        client.close();
    }
}