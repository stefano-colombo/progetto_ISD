package dominio.rabbitmp;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;


@Component
public class Producer_x{
	
	@Autowired
	private Queue queue;
	
	@Autowired
	private RabbitTemplate rabbit;
	
	Scanner tastiera=new Scanner(System.in);
	
	//@Scheduled(fixedDelay = 1000, initialDelay = 500)

	public void run(String mess,String temp, boolean messaggio) {	
		if(messaggio==false)inviaFile(mess,temp);
		else rabbit.convertAndSend(queue.getName(), mess);
	}
	
	
	public void inviaFile(String mess,String temp) {
		byte[] fileData ; // get content from file as byte[]  [Refer Here][1]
		MessageProperties mp=new MessageProperties();
		mp.setContentType(MessageProperties.CONTENT_TYPE_BYTES);
		FileInputStream inputStream = null;
		
		//invio del nome e del tipo di estensione
		String parti[]=temp.split("\\.");
		System.out.println(parti[0]);
		if(parti.length>1)
		rabbit.convertAndSend(queue.getName(), "tipo:"+parti[1]);
		rabbit.convertAndSend(queue.getName(), "nome:"+parti[0]);
		
		//invio del pacchetto
		try {
			inputStream = new FileInputStream(mess);
			fileData=inputStream.readAllBytes();
			rabbit.send(queue.getName(),new Message(fileData,mp));
		} catch (IOException e) {
			System.out.println("Errore conversione in byte"+e);
			e.printStackTrace();
		}finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String estenzione(String s) {
		
		String[] a=s.split("/");
		if(a.length<=0)return "txt";
		String b=a[a.length-1];
		String[] c=b.split("\\.");
		if(c.length<=0)return "txt";
		
		
		return c[c.length-1];
	}
	
	public String nomefile(String s) {
		
			String[] a=s.split("/");
			if(a.length<=0)return s;
			String b=a[a.length-1];
			String[] c=b.split("\\.");
			if(c.length<=0)return b;
			String temp="";
			for(int i=0;i<=c.length-2;i++)
				temp+=c[i];
			return temp;
		
	}
	
	
}
