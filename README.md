# Unité de traitement et de stockage des données


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


Let's go !
```
$ java -classpath .\JARs\*  -jar .\target\rest-service-0.0.1-SNAPSHOT.jar 192.168.1.179 admin yazid 
 _____              _                        _                                                _                           _ _
|_   _|            | |                      | |     ___                                      (_)                         (_) |
  | |_ __ ___  __ _| |_ _ __ ___   ___ _ __ | |_   ( _ )    _ __  _ __ ___   ___ ___  ___ ___ _ _ __   __ _   _   _ _ __  _| |_
  | | '__/ _ \/ _` | __| '_ ` _ \ / _ \ '_ \| __|  / _ \/\ | '_ \| '__/ _ \ / __/ _ \/ __/ __| | '_ \ / _` | | | | | '_ \| | __|
  | | | |  __/ (_| | |_| | | | | |  __/ | | | |_  | (_>  < | |_) | | | (_) | (_|  __/\__ \__ \ | | | | (_| | | |_| | | | | | |_
  \_/_|  \___|\__,_|\__|_| |_| |_|\___|_| |_|\__|  \___/\/ | .__/|_|  \___/ \___\___||___/___/_|_| |_|\__, |  \__,_|_| |_|_|\__|
                                                           | |       Yazid - ESIR PROJ-SI              __/ |
                                                           |_|            2022-2023                   |___/

[*] Running ...
[*] Listening on PositionAllBus...
[*] Raw JSON data received. 
[*] Formatting ...
Number of entries processed: 500
[*] JSON data formated and sent to 192.168.1.179 on queue PositionAllBusProcessed.
[*] Raw JSON data received. 
[*] Formatting ...
Number of entries processed: 500
[*] JSON data formated and sent to 192.168.1.179 on queue PositionAllBusProcessed.
```
<b> <div style="color: orange;">Warning : Spring tourne sur le port 8090 ! </div></b>


<h2>Data processing</h2>
L'unité de traitement reçoit, transforme et envoie des données, depuis et vers RabbitMQ.<br>
Format reçu :

```
{
    "nhits": 443,
    "parameters": {
        "dataset": "tco-bus-vehicules-position-tr",
        "rows": 500,
        "start": 0,
        "format": "json",
        "timezone": "UTC"
    },
    "records": [{
      "datasetid": "tco-bus-vehicules-position-tr",
      "recordid": "8ebcae8398538cf228cdd0392e7ea225f92814c4",
      "fields": {
        "sens": 0,
        "numerobus": -25729440,
        "numerobuskr": 212,
        "voiturekr": "0232",
        "idbus": "-25729440",
        "idligne": "0002",
        "etat": "En ligne",
        "destination": "Haut Sanc\u00e9",
        "nomcourtligne": "C2",
        "ecartsecondes": 169,
        "coordonnees": [48.093903, -1.638609]
      },
      "geometry": {
        "type": "Point",
        "coordinates": [-1.638609, 48.093903]
      },
      ...
    ]}
```

Format envoyé : 
```
{
'type': 'FeatureCollection',
'features': [
  ...
{
'type': 'Feature',
    'properties' : {
        'icon': 'bus',
        'line': "C2"
    },
    'geometry' : {
       'type': 'Point',
       'coordinates' : [-1.638609, 48.093903]
    }
},
...
```

<h2>REST API</h2>
Test GET:

```
curl localhost:8090/greeting?name="yazid"
```

TODO

<h2>Database</h2>
TODO
