package dominio.rabbitmp;


import java.io.FileOutputStream;
import java.io.IOException;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import dominio.filestorage.FileStorageImpl;
import dominio.messaggio.MessaggioService;
import dominio.messaggio.Messaggio;


@Component 
public class Consumer_x {
	@Autowired
	FileStorageImpl fileStorage;
	
	@Autowired
	MessaggioService messaggioService;
	
	String percorsoFileRicevuti;
	String destinatario;
	
	@Autowired
	public Consumer_x(@Value("${custom.percorsoFileRicevuti}") String percorsoFileRicevuti,@Value("${custom.destinatario}") String destinatario) {
		this.percorsoFileRicevuti=percorsoFileRicevuti;
		this.destinatario=destinatario;
	}
	
	String estensione="",nomefile="";
	
	
	@RabbitListener(queues = "${coda.consumatore}" )
	public void run(Message message) throws InterruptedException,IOException {
		
		//file in arrivo
		if(new String(message.getBody()).split(":")[0].equals("tipo")) { 
			//tipo del file
			estensione=getInfo(message);
		}
		else if(new String(message.getBody()).split(":")[0].equals("nome")) { 
			//nome del file
			nomefile=getInfo(message);
		}
		else if(!nomefile.equals("")){
			System.out.println(" \t\t\t\t\t\t\t\t[received]: ARRIVO di un FILE'");
			
			//salvataggio del file
			storeFile(message,nomefile,estensione);
			
			//savaltaggio del file sul database
			//l'if serve per differenziare i file che hanno un estensione da quelli che non ce l'hanno come il file Dockerfile
			String nome="";
			if(estensione.equals(""))nome=nomefile;
			else nome= nomefile+"."+estensione;
			
			messaggioService.addMessaggio(new Messaggio(destinatario,"vuoto","file",nome,estensione,"url"));
			estensione="";
			nomefile="";		
		}
		//messaggio in arrivo
		else {
			String body=new String(message.getBody());
			System.out.println(" \t\t\t\t\t\t\t\t[received]: '" +body+ "'");
			
			//savaltaggio del messaggio sul database
			messaggioService.addMessaggio(new Messaggio(destinatario,body,"messaggio","vuoto","vuoto","vuoto"));

		}
		
	}
	

	public void storeFile(Message message,String nomefile,String estensione) throws IOException {
		try (FileOutputStream outputStream = new FileOutputStream(percorsoFileRicevuti+nomefile+"."+estensione)) {
			outputStream.write(message.getBody());
		}catch(IOException e){
			System.out.println("Errore nello store del file su cartella: "+e);
		}
	}
	
	public String getInfo(Message message) {
		String tmp=new String(message.getBody()).toString();
		String info=tmp.split(":")[1];
		return info;
	}

	
}
