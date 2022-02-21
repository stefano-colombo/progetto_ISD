
package dominio.uploaddownload;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;


import dominio.filestorage.FileStorageImpl;
import dominio.messaggio.Messaggio;
import dominio.messaggio.MessaggioService;
import dominio.rabbitmp.*;

@Controller
public class UploadDownloadController {

	@Autowired
	FileStorageImpl fileStorage;
	
	@Autowired
	Producer_x producer;
	
	@Autowired
	private MessaggioService messaggioService;
	
	private String utente;
	
	@Autowired
	public UploadDownloadController(@Value("${custom.mittente}") String utente){
		this.utente=utente;
	}
	
	
   @GetMapping("/")
    public String listaMessaggi(Model model) throws IOException {
	   
	   //se è un file che mi è stato inviato gli genero l'url
	   List<Messaggio> list=messaggioService.getAllMessaggi();
	   
	   
	   //generiamo il link dei file che abbiamo
	   for(Messaggio m:list) {
	           String url = MvcUriComponentsBuilder.fromMethodName(UploadDownloadController.class,
                       "downloadFile",m.getNome() ).build().toString();
			   m.setLink(url);
	   }
	   
	   	model.addAttribute("messaggi",list);
        return "interfaccia_chat";
    }
	   
   	
	@PostMapping("/messaggio")
    public String InvioMessaggio(Messaggio e, Model model) {
    	if (e.getId() != null) {
			
			model.addAttribute("Messaggio",null);
		}
		//mando il messaggio al producer e salvo il messaggio nel database
    	producer.run(e.getContenuto(),"vuoto", true);
		return "redirect:";
	}
	
	
    @PostMapping("/")
    public String InvioFile(@RequestParam("files") MultipartFile[] files, Model model) throws IOException {
    	
		try {
	        Arrays.asList(files).stream()
				                .map(file -> {
							                	fileStorage.store(file);
							                	
							                	//invio al producer che manda il file sulla coda rabbitmq
							                	producer.run(fileStorage.getPercorso()+file.getOriginalFilename(),file.getOriginalFilename(), false);
							                	
							                	return file.getOriginalFilename();
				                			})
				                .collect(Collectors.toList());
			
			model.addAttribute("message", "File caricato con successo!");
		} catch (Exception ex) {
			model.addAttribute("message", "Fail! errore nel caricamento");
		}
		
        return "redirect:";
    }
    
    //Download Files
	@GetMapping("/{filename}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
		Resource file = fileStorage.loadFile(filename);
		return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
					.body(file);	
	}
}