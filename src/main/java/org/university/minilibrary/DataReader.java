package org.university.minilibrary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class DataReader extends Thread {
    private final SocketChannel client;
    private final String directory;
    long count = 0;
    String fileName;

    public DataReader(SocketChannel client, String directory) {
        this.client = client;
        this.directory = directory;
    }

    @Override
    public void run() {
        try{
            System.out.println("Thread started. Reading data...");
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            client.read(buffer);
            buffer.flip();
            String[] message = new String(buffer.array(), 0, buffer.limit()).split("\r\n");
            String filename = message[0];
            System.out.println("filename: " + filename);
            fileName = filename;
            long length = Long.parseLong(message[1]);
            System.out.println("length: " + length);

            File file = new File(directory + "\\" + filename);
            FileOutputStream fos = new FileOutputStream(file);
            buffer.flip();
            long count = 0;
            if(message.length == 3) {
                fos.write(message[2].getBytes(), 0, message[2].length());
                buffer.clear();
                count = message[2].length();
            }
            System.out.println(message.length);
            while (count < length) {
                int bytesRead = client.read(buffer);
                count += bytesRead;
                buffer.flip();
                fos.write(buffer.array(), 0, bytesRead);
                buffer.clear();
            }
            fos.flush();
            fos.close();
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
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void start(){
//        System.out.println("Thread started");
//        if(thread == null){
//            thread = new Thread(this);
//            thread.start();
//        }
//    }
}
