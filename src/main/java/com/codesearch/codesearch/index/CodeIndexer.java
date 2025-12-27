package com.codesearch.codesearch.index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import com.codesearch.codesearch.records.IndexedLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CodeIndexer {

    // private final Path indexPath;

    // public CodeIndexer(Path indexPath) {
    //     this.indexPath = indexPath;
    // }

    public static void createIndex(Path indexPath, List<IndexedLine> lines) {
        try {
            System.out.println("CodeIndexer : createIndex : indexPath : " + indexPath);
            System.out.println("CodeIndexer : createIndex : docsToCreate : " + lines.size());           

            IndexWriter indexWriter = createWriter(indexPath) ;
            boolean isPrint = true ;
            for(IndexedLine line : lines) {        
                if(isPrint) {
                    isPrint = false ;
                    System.out.println("CodeIndexer : createIndex : inLoop : " 
                        + line.content()
                        + " , "
                        + line.filename()
                        + " , "
                        + line.lineNumber()
                    );
                }

                Document doc = new Document() ;

                if(line.lineNumber() != 0) {
                    doc.add(new TextField("content", line.content(), Field.Store.YES)) ;
                    doc.add(new StringField("filename", line.filename(), Field.Store.YES)) ;
                    doc.add(new StoredField("line", line.lineNumber())) ;
                }
                else {
                    doc.add(new TextField("content", line.content(), Field.Store.NO)) ;
                    doc.add(new StringField("filename", line.filename(), Field.Store.YES)) ;
                }
                indexWriter.addDocument(doc) ;
            }
            
            indexWriter.commit() ;
            indexWriter.close() ;

        } catch(IOException ioe) {
            System.out.println("CodeIndexer : createIndex : ");
        }
    }

    private static IndexWriter createWriter(Path indexPath) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        // Directory directory = NIOFSDirectory.open(indexPath);
        Directory directory = new NIOFSDirectory(indexPath);

        return new IndexWriter(directory, config);
    }
}