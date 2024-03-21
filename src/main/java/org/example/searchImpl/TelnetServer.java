package org.example.searchImpl;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;

public class TelnetServer {

    public static void search (String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java TelnetFileSearchServer <rootPath> <serverPort>");
            return;
        }

        String rootPath = args[0];
        int serverPort = Integer.parseInt(args[1]);

        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            System.out.println("Telnet server is running on port " + serverPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                Thread clientThread = new Thread(() -> handleClient(rootPath, clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(String rootPath, Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            writer.println("Welcome to the File Search Server. Enter depth and mask:");

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                if (inputLine.equals("exit")) {
                    break;
                }

                String[] params = inputLine.split("\\s+");
                if (params.length != 2) {
                    writer.println("Invalid input format. Please enter depth and mask.");
                    continue;
                }

                int depth = Integer.parseInt(params[0]);
                String mask = params[1];

                searchFiles(rootPath, depth, mask, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void searchFiles(String rootPath, int depth, String mask, PrintWriter writer) {
        Queue<File> queue = new ArrayDeque<>();
        File rootDirectory = new File(rootPath);

        if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
            writer.println("Invalid root directory path.");
            return;
        }

        queue.offer(rootDirectory);

        while (!queue.isEmpty() && depth >= 0) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                File current = queue.poll();
                if (current.isDirectory() && depth > 0) {
                    File[] files = current.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            queue.offer(file);
                        }
                    }
                } else if (current.getName().contains(mask)) {
                    writer.println(current.getAbsolutePath());
                }
            }
            depth--;
        }
    }
}