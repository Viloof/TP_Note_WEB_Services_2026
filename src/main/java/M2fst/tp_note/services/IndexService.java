package M2fst.tp_note.services;

import M2fst.tp_note.exceptions.InternalServerError;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class IndexService
{

    private DirectoryReader directoryReader;
    private IndexSearcher indexSearcher;
    private final IndexWriter indexWriter;
    private final QueryParser evaluationQueryParser;

    public IndexService() throws IOException
    {
        String    indexDirectoryPath = "index";
        Path path               = Paths.get(indexDirectoryPath);
        Directory index              = FSDirectory.open(path);

        StandardAnalyzer analyzer = new StandardAnalyzer();
        this.evaluationQueryParser = new QueryParser("content", analyzer);

        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        this.indexWriter = new IndexWriter(index, indexWriterConfig);

        if (DirectoryReader.indexExists(index))
        {
            this.directoryReader = DirectoryReader.open(index);
            this.indexSearcher = new IndexSearcher(directoryReader);
        }
        else
        {
            // si l'index n'existe pas on le creé
            this.indexWriter.commit();
            this.directoryReader = DirectoryReader.open(index);
            this.indexSearcher = new IndexSearcher(directoryReader);
        }
    }

    public void indexEvaluation(Long id, Long evaluationId, String evaluation)
    {
        try
        {
            Document document = new Document();
            document.add(new LongField("evaluationId", evaluationId, Field.Store.YES));
            document.add(new StringField("restaurantId", String.valueOf(id), Field.Store.YES));
            document.add(new TextField("content", evaluation, Field.Store.YES));
            this.indexWriter.addDocument(document);
            this.indexWriter.commit();

            DirectoryReader newDirectoryReader = DirectoryReader.openIfChanged(this.directoryReader);
            if (newDirectoryReader != null)
            {
                this.directoryReader.close();
                this.directoryReader = newDirectoryReader;
                this.indexSearcher = new IndexSearcher(this.directoryReader);
            }
        }
        catch (IOException e)
        {
            throw new InternalServerError("Erreur lors de la recherche des évaluations", e);
        }
    }

    public List<String> searchEvaluationsByResto(Long id, String query)
    {
        try
        {
            Query luceneQuery = evaluationQueryParser.parse(query);
            TopDocs topDocs = indexSearcher.search(luceneQuery, 10);

            List<String> results = new java.util.ArrayList<>();
            for (org.apache.lucene.search.ScoreDoc scoreDoc : topDocs.scoreDocs)
            {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                if (doc.get("restaurantId").equals(String.valueOf(id))) {
                    results.add(doc.get("evaluationId") + ": " + doc.get("content"));
                }
            }

            return results;
        }
        catch (Exception e)
        {
            throw new InternalServerError("Erreur lors de la recherche des évaluations", e);
        }
    }


    public List<String> searchEvaluations(String query)
    {
        try
        {
            Query luceneQuery = evaluationQueryParser.parse(query);
            TopDocs topDocs = indexSearcher.search(luceneQuery, 10);

            List<String> results = new java.util.ArrayList<>();
            for (org.apache.lucene.search.ScoreDoc scoreDoc : topDocs.scoreDocs)
            {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                results.add(doc.get("evaluationId") + ": " + doc.get("content"));
            }
            return results;
        }
        catch (Exception e)
        {
            throw new InternalServerError("Erreur lors de la recherche des évaluations", e);
        }
    }
}