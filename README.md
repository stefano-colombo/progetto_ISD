# progetto_ISD
## Chat asincrona springboot+thymeleaf+mariadb+rabbitmq
il progetto presenta 4 container in tutto, 1 container rabbitmq, 1 container maradb e 2 container ubuntu configurati per eseguire un'applicazione springboot.

I dati relativi alla chat vengono memorizzati sul database mariadb. Se ad essere inviato è un messaggio, viene memoriazzato il testo, invece se si tratta di un file, viene memorizzato il nome, l'estensione e il link che viene utlizzato dall'interfaccia thymeleaf per permettere il download dei file, in entrambi i casi viene memorizzato il mittente e il tipo di messaggio (file o messagio). I dati vengono memorizzati una volta che il messaggio arriva a destinazione, non prima.


![alt text](https://github.com/stefano-colombo/progetto_ISD/blob/main/immagini/struttura_progetto.png)

Questa è la struttura del progetto,i due container utilizzano le code fornite dal broker dei messaggi per comunicare, i messaggi come scritto sopra vengono memorizzati in un database mariadb che ha la cartella /var/lib/mysql mappata con una cartella all'interno dell'host così da non perdere i dati una volta che il container viene distrutto o riavviato, i file ricevuti dalla comunicazione tra i due container spring boot vengono memorizzati nella cartella download dentro il container che viene mappata con una cartella nell'host così da verificare l'effettiva ricezioni dei file.

![alt_text](https://github.com/stefano-colombo/progetto_ISD/blob/main/immagini/chat_spring_boot.png)

Questa è l'interfaccia che permette di fornire thymeleaf, un'interfaccia abbastanza semplice dove a sinistra abbiamo la possibilità di inviare messaggi o file e a destra abbiamo lo storico della chat, i file che vengono inviati danno la possibilità di essere scaricati sul proprio host cliccando sul loro nome.

## comando per il container mariadb
docker run -it  --rm -p 4002:3306 --name db \
--mount type=bind,src=/cartella_sul_pc,dst=/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=1234 -e MYSQL_DATABASE=chat \
-e MYSQL_USER=stefano -e MYSQL_PASSWORD=ste \
mariadb

## conmando per il container rabbitmq definito nel file docker-compose.yml
docker-compose up

version: '3'  

services:
  rabbitmq:
    image: 'rabbitmq:management-alpine'
    ports:
      - '5672:5672'     # The standard AMQP protocol port
      - '15672:15672'   # HTTP management UI (guest:guest)


## creazione e esecuzione container spring boot(serve una guida perchè non è tutto immediato)
Per creare il container che possa far eseguire l'app spring boot prima bisogna creare un immaggine "base".

con il Dockerfile sottostante usiamo il comando: docker build . -t base_ubuntu

FROM ubuntu:latest
RUN apt-get update -y
RUN apt-get upgrade -y
RUN mkdir /home/download
RUN mkdir /home/upload

dopodichè bisogna attaccarsi al container in esecuzione, installare openjdk e poi creare un immagine dal container
facciamo partire il container: docker run -it --name base base_ubuntu
dentro il container installiamo openjdk: apt-get install default-jdk 
	durante l'istallazione bisognerà digitare: y 8 e 41 in seguito alle domande che compaiono
creiamo un immagine dal container: docker commit base base_openjdk

con il Dockerifle sottostante creiamo l'immagine del container che ci interessa: docker build . -t container_spring_boot
progetto.jar è il file ottentuto dopo il comando "mvn clean package" nella cartella target del progetto spring boot.


FROM parte_base_openjdk
COPY progetto.jar /
CMD ["java","-jar","progetto.jar"]

infine eseguiamo
docker run -it --rm -p 8080:8080  \
--mount type=bind,src=/cartella_sul_pc,dst=/cartella_container \
-e CUSTOM_MITTENTE=partecipante1 \
-e CUSTOM_DESTINATARIO=partecipante2 \
-e CODA_PRODUTTORE=coda1 \
-e CODA_CONSUMATORE=coda2 \
--name parte1 container_spring_boot

e

docker run -it --rm -p 8081:8080  \
--mount type=bind,src=/cartella_sul_pc,dst=/home/download \
-e CUSTOM_MITTENTE=partecipante2 \
-e CUSTOM_DESTINATARIO=partecipante1 \
-e CODA_PRODUTTORE=coda2 \
-e CODA_CONSUMATORE=coda1 \
--name parte1 container_spring_boot


il mount server per vedere se i file vengono effettivamente ricevuti dal container,la prima coppia di variabili d'ambiente serve per indicare quale ruoto si ha nella chat e la seconda coppia invece serve per indicare i nomi delle code rabbitmq.


## ALTRI utili utilizzati:

#comando per trovare l'ip dei container
docker inspect db | grep IPAddress\":

#comando per skippare la fase di test 
mvn clean package -Dmaven.test.skip=true

#esecuzione container mariadb sul mio pc
docker run -it  --rm -p 4002:3306 --name db \
--mount type=bind,src=/home/stefano/Scrivania/"progetto I.dei.S.D"/rabbitmp/container2/mariadb,dst=/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=1234 \
-e MYSQL_DATABASE=chat \
-e MYSQL_USER=stefano \
-e MYSQL_PASSWORD=ste \
mariadb

#esecuzione sul mio pc dei container spring boot
#parte1
docker run -it --rm -p 8080:8080 \
--mount type=bind,src=/home/stefano/Scrivania/"progetto I.dei.S.D"/rabbitmp/container2/parte1/home,dst=/home/download 
-e CUSTOM_MITTENTE=partecipante1 \
-e CUSTOM_DESTINATARIO=partecipante2 \
-e CODA_PRODUTTORE=coda1 \
-e CODA_CONSUMATORE=coda2 \
--name parte1 container_spring_boot


#parte2
docker run -it --rm -p 8081:8080 \
--mount type=bind,src=/home/stefano/Scrivania/"progetto I.dei.S.D"/rabbitmp/container2/parte2/home,dst=/home/download \
-e CUSTOM_MITTENTE=partecipante2 \
-e CUSTOM_DESTINATARIO=partecipante1 \
-e CODA_PRODUTTORE=coda2 \
-e CODA_CONSUMATORE=coda1 \
--name parte2 container_spring_boot



