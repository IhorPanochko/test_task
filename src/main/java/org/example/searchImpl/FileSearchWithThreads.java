package org.example.searchImpl;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;

public class FileSearchWithThreads {

    public static void search(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java FileSearchWithThreads <rootPath> <depth> <mask>");
            return;
        }

        String rootPath = args[0];
        int depth = Integer.parseInt(args[1]);
        String mask = args[2];

        Queue<File> queue = new ArrayDeque<>();
        Queue<File> resultsQueue = new ArrayDeque<>();

        Thread searchThread = new Thread(() -> {
            searchFiles(rootPath, depth, mask, queue, resultsQueue);
            resultsQueue.offer(new File(""));
        });

        Thread printThread = new Thread(() -> {
            printResults(resultsQueue);
        });

        searchThread.start();
        printThread.start();

        try {
            searchThread.join();
            printThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void searchFiles(String rootPath, int depth, String mask, Queue<File> queue, Queue<File> resultsQueue) {
        addDirectoryToQueue(rootPath, queue);
        while (!queue.isEmpty() && depth >= 0) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                File current = queue.poll();
                if (current.isDirectory()) {
                    addDirectoryToQueue(current.getAbsolutePath(), queue);
                } else if (current.getName().contains(mask)) {
                    resultsQueue.offer(current);
                }
            }
            depth--;
        }
    }

    private static void addDirectoryToQueue(String path, Queue<File> queue) {
        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    queue.offer(file);
                }
            }
        }
    }

    private static void printResults(Queue<File> resultsQueue) {
        while (true) {
            synchronized (resultsQueue) {
                File file = resultsQueue.poll();
                if (file == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                if (file.getAbsolutePath().isEmpty()) break;
                System.out.println(file.getAbsolutePath());
            }
        }
    }
}