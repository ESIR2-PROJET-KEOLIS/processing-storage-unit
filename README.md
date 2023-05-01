# Unité de traitement et de stockage des données

<img width="100%" src="https://i.ibb.co/q9849JR/image.png">
 
<img width="100%" src="https://i.ibb.co/XbhrknK/diag-seq-PU.png">

<img src="https://i.ibb.co/grLVYxG/image.png">

```
2023-05-01 12:34:44  _____              _                        _                                                _                           _ _   
2023-05-01 12:34:44 |_   _|            | |                      | |     ___                                      (_)                         (_) |  
2023-05-01 12:34:44   | |_ __ ___  __ _| |_ _ __ ___   ___ _ __ | |_   ( _ )    _ __  _ __ ___   ___ ___  ___ ___ _ _ __   __ _   _   _ _ __  _| |_ 
2023-05-01 12:34:44   | | '__/ _ \/ _` | __| '_ ` _ \ / _ \ '_ \| __|  / _ \/\ | '_ \| '__/ _ \ / __/ _ \/ __/ __| | '_ \ / _` | | | | | '_ \| | __|
2023-05-01 12:34:44   | | | |  __/ (_| | |_| | | | | |  __/ | | | |_  | (_>  < | |_) | | | (_) | (_|  __/\__ \__ \ | | | | (_| | | |_| | | | | | |_ 
2023-05-01 12:34:44   \_/_|  \___|\__,_|\__|_| |_| |_|\___|_| |_|\__|  \___/\/ | .__/|_|  \___/ \___\___||___/___/_|_| |_|\__, |  \__,_|_| |_|_|\__|
2023-05-01 12:34:44                                                            | |       Yazid & Valere - ESIR PROJ-SI     __/ |                    
2023-05-01 12:34:44                                                            |_|            2022-2023                   |___/                     
2023-05-01 12:34:44 
2023-05-01 12:34:44 [*] Running ... 
2023-05-01 12:34:44 db
2023-05-01 12:34:44 [*] Tentative de connexion à la base de données..
2023-05-01 12:34:45 [*] Connexion réussie à la base de données.
2023-05-01 12:35:16 [*] Listening on PositionAllBus...
2023-05-01 12:35:16 [*] Listening on GTFS...
2023-05-01 12:35:16 [*] Received URL : https://eu.ftp.opendatasoft.com/star/gtfs/GTFS_2022.9.0.0_20230501_20230611.zip
2023-05-01 12:35:16 [*] Listening on PARCOURS...
2023-05-01 12:35:16 [*] Raw JSON data received on PARCOURS
2023-05-01 12:35:17 [*] Début de la récupération de l'archive.
2023-05-01 12:35:18 [*] Processing ...
2023-05-01 12:35:22 [*] Data received on PARCOURS successfully processed.
2023-05-01 12:35:24 [*] Fin de la récupération de l'archive.
2023-05-01 12:35:24 [*] Archive GTFS.zip prête.
2023-05-01 12:35:26 [*] Extraction completed successfully.
2023-05-01 12:35:28 [*] Processing of file feed_info.txt
2023-05-01 12:35:28 [*] Processing of file agency.txt
2023-05-01 12:35:28 [*] Processing of file calendar_dates.txt
2023-05-01 12:35:28 [*] Processing of file stop_times.txt
2023-05-01 12:35:36 [BUSY] THE DATABASE IS CURRENTLY BEING FILLED, NO DATA PROCESSED.
2023-05-01 12:40:04 [*] Processing of file stops.txt
2023-05-01 12:40:04 [*] Processing of file trips.txt
2023-05-01 12:40:22 [*] Processing of file calendar.txt
2023-05-01 12:40:22 [*] Processing of file shapes.txt
2023-05-01 12:40:24 [BUSY] THE DATABASE IS CURRENTLY BEING FILLED, NO DATA PROCESSED.
2023-05-01 12:40:35 [*] Processing of file routes.txt
2023-05-01 12:40:35 [*] Processing of file fare_attributes.txt
2023-05-01 12:40:44 [BUSY] THE DATABASE IS CURRENTLY BEING FILLED, NO DATA PROCESSED.
2023-05-01 12:41:56 [!] ERREUR LORS DE L'INSERTION DANS LA BDD.
2023-05-01 12:41:56 [*] Base de données remplie avec succès ! 
2023-05-01 12:42:07 [*] Raw JSON data received on PositionAllBus
2023-05-01 12:42:07 [*] Formatting ...
2023-05-01 12:42:07 Number of entries in the array: 457
2023-05-01 12:42:07 [*] JSON data formated and sent to rabbitmq on queue PositionAllBusProcessed.
```


Lancez l'application dans le même répértoire que les fichiers .jar disponibles sur ce repository dans le dossier JARs/.
<br><br>Si pas de login et mdp pour le serveur RabbitMQ : 
```
java -classpath .\JARs\*  -jar .\target\rest-service-0.0.1-SNAPSHOT.jar <HOST>
```
Sinon : 
```
java -classpath .\JARs\*  -jar .\target\rest-service-0.0.1-SNAPSHOT.jar <HOST> <USERNAME> <PASSWORD>
```

Pour le docker compose :
```
java -classpath JARs\*.jar -jar target/rest-service-0.0.1-SNAPSHOT.jar 127.0.0.1 guest guest
```

Si exception `NoRouteToHostException` levée, vérifiez la configuration réseau et autorisez le port 5672 au niveau du firewall Linux : 

```
ufw allow amqp
```
ou
```
ufw allow 5672
```
