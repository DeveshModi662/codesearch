package com.codesearch.codesearch.util;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import com.codesearch.codesearch.records.IndexedLine;

public class RepoScanner {

    private static final List<String> ALLOWED_EXTENSIONS =
            List.of(".java", ".js", ".ts", ".css", ".html");

    public static List<IndexedLine> scan(Path root) throws IOException {
        List<IndexedLine> result = new ArrayList<>();

        Files.walk(root)
                .filter(Files::isRegularFile)
                .filter(RepoScanner::isAllowedFile)
                .filter(path -> !isIgnored(path))
                .forEach(file -> {
                    try {
                        System.out.println("RepoScanner : filters : filename : " + file.getFileName());
                        List<String> lines = Files.readAllLines(file) ;
                        for(int i = 0 ; i < lines.size() ; i++) {
                            result.add(new IndexedLine(file.getFileName().toString(), i+1, lines.get(i))) ;
                        }
                        String fullContent = String.join("\n", lines);  
                        result.add(new IndexedLine(file.getFileName().toString(), 0, fullContent));
                    } catch(IOException e) {
                        System.out.println("RepoScanner : ");
                    }
                });
        System.out.println("RepoScanner : filters : totalDocs : " + result.size()) ;
        return result;
    }

    private static boolean isAllowedFile(Path path) {
        String name = path.toString().toLowerCase();
        return ALLOWED_EXTENSIONS.stream().anyMatch(name::endsWith);
    }

    private static boolean isIgnored(Path path) {
        String p = path.toString();
        return p.contains(".git")
                || p.contains("node_modules")
                || p.contains("target");
    }
}
