package com.codesearch.codesearch.cli;
import com.codesearch.codesearch.index.CodeIndexer;
import com.codesearch.codesearch.records.IndexedLine;
import com.codesearch.codesearch.services.SearchService;
import com.codesearch.codesearch.util.RepoScanner;
// import org.apache.lucene.index.IndexWriter;

// import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

// import org.apache.lucene.index.DirectoryReader;

public class SearchCLI {

    private static void handleCreate(String[] args) throws Exception {
        Path codebase = Path.of(args[1]);
        Path indexPath = Path.of(args[2]);    
        System.out.println("Creating index on " + codebase + " at " + indexPath);

        List<IndexedLine> lines = RepoScanner.scan(codebase);
        CodeIndexer.createIndex(indexPath, lines);

        System.out.println("Index created at: " + indexPath);

    }

    private static void handleSearch(String[] args, Path indexPath) throws Exception {        
        String contentQuery = args[1].substring("--content=".length()) ;
        System.out.println("SearchCLI : handleSearch : Search " + contentQuery + " from " + indexPath);
        SearchService.search(contentQuery, indexPath);        
        
    }
    public static void main(String[] args) throws Exception {

        Path indexPath = Path.of("C:\\Users\\deves\\OneDrive\\Documents\\Deleteme\\DoroIndex") ;

        switch(args[0]) {
            case "create" :
                handleCreate(args) ;
                break ;
            case "search" :
                handleSearch(args, indexPath) ;
                break ;
        }

        // Path repoPath = Path.of("C:\\Users\\deves\\OneDrive\\Documents\\Deleteme\\Doro") ;
        // Path repoPath = Path.of(args[0]) ;

        // Path indexPath = Path.of("C:\\Users\\deves\\OneDrive\\Documents\\Deleteme\\DoroIndex") ;

        // List<Path> files = RepoScanner.scan(repoPath);
        // System.out.println("Files found from SearchCLI: " + files.size());

        // try {
			// RepoScanner scanner = new RepoScanner() ;
			// List<Path> output = RepoScanner.scan(repoPath) ;
			// for (Path path : files) {
			// 	System.out.println(path.getFileName());
			// }
		// } catch(IOException ioe) {
        //     System.out.println("Caught error : ");
		// 	System.out.println(ioe.toString());
		// }
		

        // CodeIndexer indexer = new CodeIndexer(indexPath);
        // IndexWriter writer = indexer.createWriter();

        // No documents yet
        // writer.close();

        // System.out.println("Empty index created at from searchcli: " + indexPath.toAbsolutePath());
    }
}