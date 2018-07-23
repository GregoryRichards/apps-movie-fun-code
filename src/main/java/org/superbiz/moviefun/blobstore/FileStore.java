package org.superbiz.moviefun.blobstore;

import com.amazonaws.util.IOUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class FileStore implements BlobStore
{

    @Override
    public void put(Blob blob) throws IOException {
        File targetFile = new File(blob.name);

        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();


        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            IOUtils.copy(blob.inputStream, outputStream);
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        File file = new File(name);

        if (!file.exists()) {
            return Optional.empty();
        }

        return Optional.of(new Blob(
                name,
                new FileInputStream(file),
                Files.probeContentType( file.toPath() )
        ));
    }

    @Override
    public void deleteAll() {

    }
}
