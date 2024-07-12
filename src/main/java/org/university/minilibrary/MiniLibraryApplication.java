package org.university.minilibrary;

import java.nio.ByteBuffer;
import java.nio.channels.*;
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
        System.out.println("Reading client's message.");

        SocketChannel client = (SocketChannel)key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(8192);

        // Read the file name
        client.read(buffer);
        buffer.flip();
        String[] message = new String(buffer.array(), 0, buffer.limit()).split("\r\n");
        String filename = message[0];
        long length = Long.parseLong(message[1]);
        System.out.println("FileName: " + filename);
        if (length/(1024*1024*1024) > 1) {
            double factor = (double) length /(1024*1024*1024);
            String result = String.format("%.2f", factor);
            System.out.println("Length: " + result + " Gb");
        } else if (length/(1024*1024) > 1) {
            double factor = (double) length /(1024*1024);
            String result = String.format("%.2f", factor);
            System.out.println("Length: " + result + " Mb");
        } else if (length/1024 > 1) {
            double factor = (double) length /1024;
            String result = String.format("%.2f", factor);
            System.out.println("Length: " + result + " Kb");
        }else {
            System.out.println("Length: " + length + " b");
        }
        buffer.clear();
        File file = new File(directory + "\\" + filename);
        FileOutputStream fos = new FileOutputStream(file);

        long count = 0;
        long startTime = System.currentTimeMillis();
        long ReadInHalfSecond = 0;
        System.out.print("Data acquisition rate: ? b/sec");
        while (count < length) {
            int bytesRead = client.read(buffer);
            count += bytesRead;
            ReadInHalfSecond += bytesRead;
            long currentTime = System.currentTimeMillis();
            buffer.flip();
            fos.write(buffer.array(), 0, bytesRead);
            buffer.clear();
            if (currentTime - startTime > 500) { // Каждые 0.5 секунды
                startTime = currentTime;
                if ((ReadInHalfSecond / (512*1024*1024)) > 1) { // Изменение коследней строки на актуальную
                    double factor = (double) ReadInHalfSecond;
                    factor = factor / (512*1024*1024);
                    String resultOfSpeed = String.format("%.2f", factor);
                    System.out.print("\r");
                    System.out.print("Data acquisition rate: " + resultOfSpeed + " Gb/sec");
                    System.out.flush();

                } else if ((ReadInHalfSecond / (512*1024)) > 1) {
                    double factor = (double) ReadInHalfSecond;
                    factor = factor / (512*1024);
                    String resultOfSpeed = String.format("%.2f", factor);
                    System.out.print("\r");
                    System.out.print("Data acquisition rate: " + resultOfSpeed + " Mb/sec");
                    System.out.flush();

                } else if ((ReadInHalfSecond / 512) > 1) {
                    double factor = (double) ReadInHalfSecond;
                    factor = factor / (512);
                    String resultOfSpeed = String.format("%.2f", factor);
                    System.out.print("\r");
                    System.out.print("Data acquisition rate: " + resultOfSpeed + " Kb/sec");
                    System.out.flush();
                }else {
                    System.out.print("\r");
                    System.out.print("Data acquisition rate: " + ReadInHalfSecond * 2 + " b/sec");
                    System.out.flush();
                }
                ReadInHalfSecond = 0;
            }
        }

        fos.flush();
        client.close();

        System.out.println();
        System.out.println("File received: " + filename);
    }
}