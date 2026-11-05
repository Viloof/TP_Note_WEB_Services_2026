package M2fst.tp_note.repositories;

import M2fst.tp_note.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Repository
public class S3Repository
{
    private final String BUCKETResto = "restaurant";
    private final String BUCKETEval = "evaluation";

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    public S3Repository(@Value("${spring.s3.accessKeyId}") String accessKeyId, @Value("${spring.s3.secretAccessKey}") String secretAccessKey, @Value("${spring.s3.url}") String url)
    {
        this.s3Presigner = S3Presigner.builder()
                    .region(Region.US_EAST_1)
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.builder()
                            .accessKeyId(accessKeyId)
                            .secretAccessKey(secretAccessKey)
                            .build()))
                    .endpointOverride(URI.create(url))
                    .serviceConfiguration(S3Configuration.builder()
                            .pathStyleAccessEnabled(true)
                            .build())
                    .build();

        this.s3Client =  S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.builder()
                        .accessKeyId(accessKeyId)
                        .secretAccessKey(secretAccessKey)
                        .build()))
                .endpointOverride(URI.create(url))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();

    }

    // Récupère l'URL d'une image de restaurant depuis S3
    public String getRestoFileUrl(String filePath)
    {
        if (filePath == null || filePath.isBlank())
            throw new NotFoundException("Aucune image n'est associée à ce restaurant.");

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(BUCKETResto)
                .key(filePath)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(request)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toExternalForm();
    }


    // Récupère l'URL d'upload d'une image d'évaluation depuis S3
    public String getRestoUploadFileUrl(String filePath)
    {
        PutObjectPresignRequest request = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(PutObjectRequest.builder()
                        .bucket(BUCKETResto)
                        .key(filePath)
                        .build())
                .build();

        return s3Presigner.presignPutObject(request)
                .url()
                .toExternalForm();
    }

    // Récupère les URLs d'images d'évaluation depuis S3
    public Collection<String> getEvalFileUrls(List<String> filePaths)
    {
        List<String> urls = new ArrayList<>();

        for (String filePath : filePaths)
        {
            if (filePath == null || filePath.isBlank())
                throw new NotFoundException("Aucune image n'est associée à cette évaluation.");

            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(BUCKETEval)
                    .key(filePath)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(request)
                    .build();

            urls.add(s3Presigner.presignGetObject(presignRequest)
                    .url()
                    .toExternalForm());
        }
        return urls;
    }


    // Récupère les URLs d'upload d'images d'évaluation depuis S3
    public Collection<String> getEvalUploadFileUrls(List<String> keys)
    {
        List<String> urls = new ArrayList<>();
        for (String key : keys)
        {
            PutObjectPresignRequest request = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(PutObjectRequest.builder()
                            .bucket(BUCKETEval)
                            .key(key)
                            .build())
                    .build();

            urls.add(s3Presigner.presignPutObject(request)
                    .url()
                    .toExternalForm());
        }
        return urls;
    }


    // Supprime une image de restaurant depuis S3
    public void deleteRestoImage(String filePath)
    {
        if (filePath != null && !filePath.isBlank())
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(BUCKETResto)
                    .key(filePath)
                    .build());

    }


    // Supprime une ou plusieurs images d'évaluation depuis S3
    public void deleteEvalImages(List<String> imageKeys)
    {
        for (String imageKey : imageKeys)
        {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(BUCKETEval)
                    .key(imageKey)
                    .build());
        }
    }

}
