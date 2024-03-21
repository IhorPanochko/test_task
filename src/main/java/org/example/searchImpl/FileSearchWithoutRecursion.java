package org.example.searchImpl;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class FileSearchWithoutRecursion {

    public static void search(String[] args) {
        if (args.length != 3) {
            System.out.println("Input parameters should have 3 params");
            System.exit(1);
        }

        Path rootPath = Paths.get(args[0]);
        int depth = Integer.parseInt(args[1]);
        String mask = args[2];

        try {
            walkFileTree(rootPath, depth, mask);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static void walkFileTree(Path rootPath, int depth, String mask) throws IOException {
        LinkedList<Path> queue = new LinkedList<>();
        queue.add(rootPath);

        int currentDepth = 0;

        while (!queue.isEmpty() && currentDepth <= depth) {
            LinkedList<Path> nextLevel = new LinkedList<>();
            while (!queue.isEmpty()) {
                Path current = queue.poll();
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(current)) {
                    for (Path entry : stream) {
                        if (Files.isDirectory(entry) && currentDepth < depth) {
                            nextLevel.add(entry);
                        } else if (currentDepth == depth && entry.getFileName().toString().contains(mask)) {
                            System.out.println(entry.toAbsolutePath());
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error accessing directory: " + current.toAbsolutePath());
                }
            }
            queue = nextLevel;
            currentDepth++;
        }
    }
}
