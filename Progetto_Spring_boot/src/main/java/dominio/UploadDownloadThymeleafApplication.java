package dominio;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import dominio.rabbitmp.Producer_x;
import dominio.filestorage.*;


@EnableScheduling
@SpringBootApplication
public class UploadDownloadThymeleafApplication {

	//configuarazioni per la creazione della coda parte consumatore 
	@Bean
	Queue queue(@Value("${coda.produttore}") String queueName) {
		return new Queue(queueName, false);
	}

	//collegamento al broker di messaggi rabbitmq
	@Bean
	CachingConnectionFactory connectionFactory(@Value("${rabbit.indirizzo}") String indirizzo) {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(indirizzo);
	    //CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory("172.22.0.1");
	    //CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory("localhost");
	    return cachingConnectionFactory;
	}
	
	//produttore definito qui per essere richiamato poi dal controller
	@Bean
	Producer_x producer() {
		return new Producer_x();
	}
	
	
	public static void main(String[] args) {
		SpringApplication.run(UploadDownloadThymeleafApplication.class, args);
	}

}
