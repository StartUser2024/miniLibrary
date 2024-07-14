package org.university.minilibrary;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.*;

public class MiniLibraryApplication {
    private static Selector selector = null;
    //private static final Logger logger = LoggerFactory.getLogger(MiniLibraryApplication.class);

    public static void main(String[] args) {
        try {
            Scanner in = new Scanner(System.in);
            System.out.print("Input IP: ");
            String ip = in.nextLine();
            //String ip = "127.0.0.1";
            System.out.print("Input port: ");
            int port = in.nextInt();
            //int port = 808;
            System.out.print("Input directory: ");
            in.nextLine();
            String directory = in.nextLine();
            //String directory = "C:\\Users\\User\\Downloads\\Arhiv";
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


    private static void handleRead(SelectionKey key, String directory) throws IOException {

        SocketChannel client = (SocketChannel)key.channel();

        DataReader dataReader = new DataReader();

        dataReader.client = client;
        dataReader.directory = directory;

        ExecutorService executor1 = Executors.newSingleThreadExecutor();
        Future<?> future = executor1.submit(dataReader);


        DataSpeedCalculator dataSpeedCalculator = new DataSpeedCalculator();

        dataSpeedCalculator.dataReader = dataReader;

        ScheduledExecutorService executor2 = Executors.newSingleThreadScheduledExecutor();
        executor2.scheduleAtFixedRate(dataSpeedCalculator, 100, 500, TimeUnit.MILLISECONDS);

        while (!future.isDone() ){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        executor1.shutdown();
        executor2.shutdown();
        client.close();
        System.out.println("Connection Closed..");
    }
}