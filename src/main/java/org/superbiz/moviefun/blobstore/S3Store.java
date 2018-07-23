package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.tika.Tika;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.albums.AlbumsBean;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

public class S3Store implements BlobStore {

    private AmazonS3 s3;
    private String bucketName;

    S3Store(AmazonS3 s3, String photoStorageBucket) {
        this.s3 = s3;
        this.bucketName = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {

        s3.putObject(bucketName, blob.name, blob.inputStream, new ObjectMetadata());
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        if ( !s3.doesObjectExist(bucketName, name ) )
            return Optional.empty();

        S3Object s3Object = s3.getObject(bucketName, name);
        Blob blob = null;

        if (s3Object != null) {
            blob = new Blob(s3Object.getKey(), s3Object.getObjectContent(), s3Object.getObjectMetadata().getContentType());
        }
        return Optional.ofNullable(blob);
    }

    @Override
    public void deleteAll() {
        s3.deleteBucket(bucketName);
        s3.createBucket(bucketName);
    }
}
