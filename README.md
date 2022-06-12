# Progetto_ISD
## Chat asincrona springboot+thymeleaf+mariadb+rabbitmq
il progetto presenta 4 container in tutto, 1 container rabbitmq, 1 container maradb e 2 container ubuntu configurati per eseguire un'applicazione springboot.

I dati relativi alla chat vengono memorizzati sul database mariadb. Se ad essere inviato è un messaggio, viene memoriazzato il testo, invece se si tratta di un file, viene memorizzato il nome, l'estensione e il link che viene utlizzato dall'interfaccia thymeleaf per permettere il download dei file, in entrambi i casi viene memorizzato il mittente e il tipo di messaggio (file o messagio). I dati vengono memorizzati una volta che il messaggio arriva a destinazione, non prima.


![alt text](https://github.com/stefano-colombo/progetto_ISD/blob/main/immagini/struttura_progetto.png)

Questa è la struttura del progetto,i due container utilizzano le code fornite dal broker dei messaggi per comunicare, i messaggi come scritto sopra vengono memorizzati in un database mariadb che ha la cartella /var/lib/mysql mappata con un volume così da non perdere i dati una volta che il container viene distrutto o riavviato, i file ricevuti dalla comunicazione tra i due container spring boot vengono memorizzati nella cartella download dentro il container che viene mappata con un volume così da verificare l'effettiva ricezioni dei file.

![alt_text](https://github.com/stefano-colombo/progetto_ISD/blob/main/immagini/chat_spring_boot.png)

Questa è l'interfaccia che permette di fornire thymeleaf, un'interfaccia abbastanza semplice dove a sinistra abbiamo la possibilità di inviare messaggi o file e a destra abbiamo lo storico della chat, i file che vengono inviati danno la possibilità di essere scaricati sul proprio host cliccando sul loro nome.

![alt_text](https://github.com/stefano-colombo/progetto_ISD/blob/main/immagini/struttura_app.png)

Questa è la struttura dell'app creata con spring tool suite 4

### Far partire il progetto
Per far partire il progetto creare i container di base come descritto nella relazione, andare nella cartella "file docker compose" e lanciare docker-compose -f docker_compose_intera_struttura.yml 





