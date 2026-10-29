package app.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class Search {
    private static final String INDEX_DIRECTORY = "index";

    public static void main(String[] args) throws Exception {
        Analyzer analyzer = new PolishAnalyzer();
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        String querystr = "*:*";
        Query query = new QueryParser("title", analyzer).parse(querystr);
        int maxHits = 100;

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(query, maxHits);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " matching docs.");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document doc = searcher.storedFields().document(docId);
            System.out.println((i + 1) + ". " + doc.get("isbn") + "\t" + doc.get("title"));
        }

        /*
        1. Jak zachowa się wyszukiwanie?
        Po uruchomieniu programu Index dwa razy wyszukiwanie zwraca każdą książkę dwa razy — w indeksie jest 12 dokumentów zamiast 6.

        2. Jak zmieni się zawartość katalogu z plikami indeksu?
        W katalogu indeksu pojawiają się nowe pliki segmentów, a plik segments zostaje zaktualizowany. Lucene może później scalić segmenty.
         */

        reader.close();
        directory.close();
    }
}
