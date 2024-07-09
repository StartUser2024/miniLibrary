package org.university.minilibrary;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import lombok.SneakyThrows;
import org.json.JSONObject;
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
            String ip = in.nextLine();
            System.out.print("Input port (default 808): ");
            int port = 808;
            port = in.nextInt();
            System.out.print("Input directory: ");
            in.nextLine();
            String directory = in.nextLine();
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

//        // start time
//        long startTime = System.currentTimeMillis();
//        long totalBytesRead = 0;
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

        // create a ServerSocketChannel to read the request
        SocketChannel client = (SocketChannel)key.channel();
        // Create ByteBuffer to read data
        ByteBuffer Buffer = ByteBuffer.allocate(1000000);
        client.read(Buffer);
        String data = new String(Buffer.array()).trim();
        System.out.println("*** " + data + " ***");
        JSONObject jsonObject = new JSONObject(data);
        System.out.println("*** " + jsonObject + " ***");

        String filename = (String) jsonObject.get("fileName");
        int length = (int) jsonObject.get("length");
        byte[] payload = ((String) jsonObject.get("payload")).getBytes();

        System.out.println("FileName: " + filename);
        System.out.println("Length: " + length);
        System.out.println("Payload: " + payload);
        System.out.println("Payload: " + new String(payload));

        FileOutputStream fos = new FileOutputStream(directory + "\\" + filename);
        fos.write(payload);
        fos.close();
        System.out.println("Received message - fileName: " + filename + ", length: " + length + ", payload: " + new String(payload));
        System.out.println("File received: " + filename);
    }
}

//                ps.close();
//                br.close();
//                kb.close();
//            // Read length
//            int length = Integer.parseInt(input.readLine().trim());

//            // Read payload
//            byte[] payload = new byte[length];
//            input.readFully(payload);
//                // to send data to the client
//                PrintStream ps
//                        = new PrintStream(s.getOutputStream());
//
//                BufferedReader br
//                        = new BufferedReader(
//                        new InputStreamReader(
//                                s.getInputStream()));
//
//                // to read data from the keyboard
//                BufferedReader kb
//                        = new BufferedReader(
//                        new InputStreamReader(System.in));
                // to read data coming from the client


                // server executes continuously
//                while (true) {
//
//                    String str, str1;
//
//                    // repeat as long as the client
//                    // does not send a null string
//
//                    // read from client
//                    while ((str = br.readLine()) != null) {
//                        System.out.println(str);
//                        str1 = kb.readLine();
//
//                        // send to client
//                        ps.println(str1);
//                    }

                    // close connection
//                    ps.close();

                    // end of while


//    private ServerSocket serverSocket;
//    private Socket clientSocket;
//    private PrintWriter out;
//    private BufferedReader in;
//
//    public static void main(String[] args) {
//        String ip = "127.0.0.1", directory = "C:\\Users\\User\\Downloads";
//        int port = 0;
//
//        // Запрос IP адреса, порта и директории у пользователя
//        // (здесь не показано для краткости)
//
//        try {
//            ServerSocket serverSocket = new ServerSocket(port);
//            System.out.println("Server running on " + ip + ":" + port);
//
//            while (true) {
//                System.out.println("Waiting for the client on the port " +
//                        serverSocket.getLocalPort() + "...");
//                Socket socket = serverSocket.accept();
//                System.out.println("Просто подключается к " + socket.getRemoteSocketAddress());
//
//                InputStream inputStream = socket.getInputStream();
//
//                // Чтение данных из сокета
//                byte[] buffer = new byte[1024];
//                int bytesRead;
//                long startTime = System.currentTimeMillis();
//                long totalBytesRead = 0;
//
//                // Чтение первой строки (название файла)
//                String fileName = readLine(inputStream);
//                System.out.println("File name: " + fileName);
//
//                // Чтение второй строки (размер данных)
//                String lengthStr = readLine(inputStream);
//                System.out.println("Length: " + lengthStr);
//                int length = Integer.parseInt(lengthStr);
//
//                // Чтение данных
//                FileOutputStream fileOutputStream = new FileOutputStream(directory + "/" + fileName);
//
//                while (totalBytesRead < length) {
//                    bytesRead = inputStream.read(buffer);
//                    totalBytesRead += bytesRead;
//                    fileOutputStream.write(buffer, 0, bytesRead);
//
//                    long currentTime = System.currentTimeMillis();
//                    long elapsedTime = currentTime - startTime;
//                    double speed = totalBytesRead / (elapsedTime / 1000.0);
//                    System.out.println("Speed: " + speed + " bytes/sec");
//                }
//
//                fileOutputStream.close();
//                System.out.println("File saved successfully");
//
//                socket.close();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static String readLine(InputStream inputStream) throws IOException {
//        StringBuilder sb = new StringBuilder();
//        int c;
//        while ((c = inputStream.read()) != '\r') {
//            sb.append((char) c);
//        }
//        // пропускаем символ переноса строки
//        inputStream.read();
//        return sb.toString();
//    }
//}
