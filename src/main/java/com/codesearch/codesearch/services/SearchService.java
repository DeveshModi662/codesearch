package com.codesearch.codesearch.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
// import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queries.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.queries.spans.SpanNearQuery;
import org.apache.lucene.queries.spans.SpanQuery;
import org.apache.lucene.queries.spans.SpanTermQuery;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
// import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.BytesRef;

public class SearchService {

    public static void search(String contentQuery, Path indexPath) {
        System.out.println("SearchService : search") ;
        try {
            // DirectoryReader dirReader = DirectoryReader.open(NIOFSDirectory.open(indexPath)) ;
            DirectoryReader dirReader = DirectoryReader.open(new NIOFSDirectory(indexPath)) ;
            System.out.println("SearchService : search : indexPath " + indexPath);
            System.out.println("SearchService : search : totalDocsCount " + dirReader.numDocs());

            Terms terms = MultiTerms.getTerms(dirReader, "content");
            TermsEnum te = terms.iterator();

            BytesRef term;
            while ((term = te.next()) != null) {
                System.out.println("SearchService : search : terms : " + term.utf8ToString());
            }

            IndexSearcher searcher = new IndexSearcher(dirReader) ;
            
            Query query = buildQuery(contentQuery) ;
            System.out.println("SearchService : search : contentQuery " +contentQuery);
            System.out.println("SearchService : search : query " + query.toString());
            TopDocs results = searcher.search(query, 4) ;
            System.out.println("SearchService : search : results " + results.scoreDocs.length);
            for(ScoreDoc sd : results.scoreDocs) {
                // Document doc = searcher.doc(sd.doc, Set.of("filename", "lineNumber", "content")) ;
                System.out.println("SearchService : search : result : ");
                Document doc = searcher.storedFields().document(sd.doc) ;
                System.out.println(doc.get("filename") + " : " + doc.get("line") + " -> " + doc.get("content"));
            }

        } catch(ParseException pe) {
            System.out.println("SearchService : ParseException : " + pe.toString());
        } catch(IOException ioe) {
            System.out.println("SearchService : IOException : " + ioe.toString());
        }            
    }

    private static Query buildQuery(String input) throws ParseException {

        // Exact phrase: "exactSearch"
        if (input.startsWith("'") && input.endsWith("'")) {
            System.out.println("SearchService : buildQuery : exactMatch") ;
            QueryParser parser = new QueryParser("content", new StandardAnalyzer());
            Query q = parser.parse(input);
            
            // String phrase = input.substring(1, input.length() - 1).toLowerCase();
            // PhraseQuery.Builder builder = new PhraseQuery.Builder();

            // builder.add(new Term(phrase)) ;
            // return builder.build();
            return q ;
        }
        else if (input.contains("*")) {        // Wildcard: *wild*card*            
            System.out.println("SearchService : buildQuery : wildcard : " + input.replaceAll("^\\*+|\\*+$", "")) ;

            List<String> wildcards = Arrays.asList(input.toLowerCase().replaceAll("^\\*+|\\*+$", "").split("\\*"));            
            System.out.println("SearchService : buildQuery : wildcard : after regex") ;
            List<SpanQuery> tokens = new ArrayList<>() ;
            // for(String token : wildcards) {
            //     System.err.print(token + " ");
            //     // tokens.add(new SpanTermQuery(new Term("content", token))) ;
            //     tokens.add(new SpanMultiTermQueryWrapper<>(new WildcardQuery(new Term("content", token)))) ;
            // }
            // System.out.println("");            
            List<SpanQuery> spanTokens = wildcards.stream()
            .map(t -> new SpanMultiTermQueryWrapper<>(new WildcardQuery(new Term("content", "*"+t+"*"))))
            .collect(Collectors.toList());
            
            // SpanNearQuery spanQuery = new SpanNearQuery(tokens.toArray(new SpanQuery[0]), 100000, false) ;
            SpanNearQuery spanQuery = new SpanNearQuery(spanTokens.toArray(new SpanQuery[0]), Integer.MAX_VALUE, true) ;

            System.out.println("SearchService : buildQuery : wildcard") ;
            return spanQuery ;
            // return new WildcardQuery(
            //         new Term("content", input.toLowerCase())
            // );
        }

        // Default term search
        System.out.println("SearchService : buildQuery : default") ;
        return new TermQuery(
                new Term("content", input.toLowerCase())
        );
    }

}
