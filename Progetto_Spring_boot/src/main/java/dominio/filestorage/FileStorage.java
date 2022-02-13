package dominio.filestorage;

import java.nio.file.Path;
import java.util.stream.Stream;
 
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
 
public interface FileStorage {
	public void store(MultipartFile file);
	public Resource loadFile(String filename);
	public Stream<Path> loadFiles();
}

