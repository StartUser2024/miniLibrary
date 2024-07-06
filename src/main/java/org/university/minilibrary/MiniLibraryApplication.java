package org.university.minilibrary;

import java.nio.channels.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.*;
import java.util.Scanner;
import java.io.*;
import java.net.*;

public class MiniLibraryApplication {
    private static Selector selector = null;
    private static final Logger logger = LoggerFactory.getLogger(MiniLibraryApplication.class);

    public static void main(String args[])
    {
        try {
            // Этап подготовки
            Scanner in = new Scanner(System.in);
            System.out.print("Input IP: ");
            String ip = in.nextLine();
            System.out.print("Input port: ");
            int port = in.nextInt();
            System.out.print("Input directory: ");
            in.nextLine();
            String directory = in.nextLine();
            System.out.printf("IP: %s  Port: %d  Directory: %s \n", ip, port, directory);
            in.close();

            // Прослушка TCP соединений
            selector = Selector.open();
            ServerSocketChannel serverSocketChannel
                    = ServerSocketChannel.open();
            ServerSocket serverSocket
                    = serverSocketChannel.socket();

            serverSocket.bind(
                    new InetSocketAddress("localhost", 8089));
            serverSocketChannel.configureBlocking(false);


            ip = "127.0.0.1";
            directory = "C:\\Users\\User\\Downloads";

            // Create server Socket
            while (true){
                ServerSocket ss = new ServerSocket(port);

                // connect it to client socket
                Socket s = ss.accept();
                System.out.println("Connection established");

                // start time
                long startTime = System.currentTimeMillis();
                long totalBytesRead = 0;

                // reading frames
                DataInputStream input = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                byte[] bytes = input.readAllBytes();
                String fileContent = new String(bytes);
                System.out.println(fileContent);

                // use inputLine.toString(); here it would have whole source
                // parsing frames
                String regex = "^[a-zA-Z0-9\\.\\s]+\\r\\n\\d+\\r\\n[-\\p{ASCII}]+\\r\\n$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(fileContent);

                // logging errors
                if (matcher.matches()) {
                    System.out.println("The data frame corresponds to the required format");
                } else {
                    System.out.println("The data frame does not match the required format");
                    logger.error("Error parsing frame. Closing current channel.");
                    ss.close();
                    s.close();
                    break;
                }

                String massive[] =  fileContent.split("\\r\\n");

                // Read fileName, length and payload
                String fileName = massive[0];

                int length = Integer.parseInt(massive[1]);

                byte[] payload = new byte[length];
                payload = massive[2].getBytes();

                //Write fileName, length and payload
                System.out.println(fileName + "\r\n" + length + "\r\n" + payload.toString());

                // Save file
                FileOutputStream fos = new FileOutputStream(directory + "\\" + fileName);
                fos.write(payload);
                fos.close();

                System.out.println("File received: " + fileName);

                // finish time
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - startTime;
                double bytesReadPerSecond = ((double) totalBytesRead / (double) elapsedTime) * 1000; // Байтов в секунду
                double kilobytesReadPerSecond = bytesReadPerSecond / 1024; // Килобайтов в секунду
                System.out.printf("Speed: %.2f KB/s\n", kilobytesReadPerSecond);

                ss.close();
                s.close();
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
