package dominio.filestorage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rabbitmq.client.impl.Environment;

@Service
public class FileStorageImpl implements FileStorage{
	
	private String percorsoFileCaricati="/home/upload/";
	private String percorsoFileRicevuti="/home/download/";
	//private String percorsoFileRicevuti;
	private Path trootLocation = Paths.get(percorsoFileRicevuti);
	private final Path rootLocation = Paths.get(percorsoFileCaricati);
 
	public String getPercorso() {
		return percorsoFileCaricati;
	}
	
	
	@Override
	public void store(MultipartFile file){
		try {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
        	throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }
	}
	
	@Override
	public Resource loadFile(String filename) {
	
		try{
		    	//ricerca nella cartella upload
		    Path file = rootLocation.resolve(filename);
		    Resource resource = new UrlResource(file.toUri());
		    if(resource.exists() || resource.isReadable()) {
		       return resource;
		    }
		    else{
				 //ricerca nella cartella download
		         Path file2 = trootLocation.resolve(filename);
		         Resource resource2 = new UrlResource(file2.toUri());
		         if(resource2.exists() || resource2.isReadable()) { 
		             return resource2;
		         }
			     else{
			        throw new RuntimeException("Non puoi salvare il file!");
			      }
		    }
		    
		}catch(MalformedURLException e){
			throw new RuntimeException("Error! -> message = " + e.getMessage());
		}
		
	
	}
	

	@Override
	public Stream<Path> loadFiles() {
		
        try {
            return Files.walk(this.rootLocation, 1)
                .filter(path -> !path.equals(this.rootLocation))
                .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
        	throw new RuntimeException("\"Failed to read stored file");
        }
        
	}

}