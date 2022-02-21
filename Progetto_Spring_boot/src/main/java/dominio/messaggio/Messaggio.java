package dominio.messaggio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Messaggio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String mittente;
	private String contenuto;
	private String tipo;
	
	private String nome;
	private String estensione;
	private String link;
	
	public Messaggio() {
	}
	//se è un messaggio verranno presi in considerazioni solo i valori di mittente, contenuto e tipo
	//se è un file verranno presi in considerazioni solo i valori di mittente, nome estensione, tipo e link
	public Messaggio(String mittente,String contenuto,String tipo,String nome,String estensione,String link) {
		super();
		this.mittente = mittente;
		this.contenuto = contenuto;
		this.tipo = tipo;
		this.nome=nome;
		this.estensione=estensione;
		this.link=link;
		
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMittente() {
		return mittente;
	}

	public void setMittente(String mittente) {
		this.mittente = mittente;
	}

	public String getContenuto() {
		return contenuto;
	}

	public void setContenuto(String contenuto) {
		this.contenuto = contenuto;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEstensione() {
		return estensione;
	}

	public void setEstensione(String estensione) {
		this.estensione = estensione;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return "Messaggio [id=" + id + ", mittente=" + mittente + ", contenuto=" + contenuto + ", tipo=" + tipo
				+ ", nome=" + nome + ", estensione=" + estensione + ", link=" + link + "]";
	}


	
	

}
