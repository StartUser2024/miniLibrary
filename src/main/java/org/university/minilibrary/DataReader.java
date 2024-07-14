package org.university.minilibrary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class DataReader implements Runnable {
    public   SocketChannel client;
    public   String directory;
    long count = 0;
    String fileName;

    @Override
    public void run() {
        try{
            System.out.println("Thread started. Reading data...");
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            int bytesRead = client.read(buffer);
            buffer.flip();

            String data = new String(buffer.array(), 0, bytesRead);

            String[] message = data.split("\r\n");
            if(message.length < 3) {
                System.out.println("Error: Invalid data format received");
                client.close();
                return;
            }

            String filename = message[0];
            fileName = filename;
            long length = Long.parseLong(message[1]);

            File file = new File(directory + "\\" + filename);
            FileOutputStream fos = new FileOutputStream(file);

            if(message.length == 3) {
                fos.write(message[2].getBytes(), 0, message[2].length());
                buffer.clear();
                count = message[2].length();
            }

            while (count < length) {
                bytesRead = client.read(buffer);
                if(bytesRead == -1) {
                    System.out.print("\r");
                    System.out.println("Error: Connection closed by client");
                    client.close();
                    return;
                }
                count += bytesRead;
                buffer.flip();
                fos.write(buffer.array(), 0, bytesRead);
                buffer.clear();
            }
            fos.flush();
            fos.close();

            System.out.print("\r");
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
            // Логика чтения данных
        }catch (IOException | NumberFormatException e) {
            System.out.print("\r");
            System.out.println("Error: " + e.getMessage());
            try {
                client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
