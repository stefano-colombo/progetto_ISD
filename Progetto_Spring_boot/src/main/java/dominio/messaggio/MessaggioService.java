package dominio.messaggio;

import java.util.ArrayList;
import java.util.List;
//import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MessaggioService {

	@Autowired
	private MessaggioRepository messaggioRepository;

	public Messaggio addMessaggio(Messaggio e) {
		return messaggioRepository.save(e);
	}

	public List<Messaggio> getAllMessaggi() {
		List<Messaggio> Messaggi = new ArrayList<>();
		messaggioRepository.findAll().forEach(Messaggi::add);
		return Messaggi;
	}

}
