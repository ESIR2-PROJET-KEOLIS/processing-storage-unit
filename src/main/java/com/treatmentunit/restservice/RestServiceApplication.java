package com.treatmentunit.restservice;

import com.treatmentunit.database.DatabaseBinding;
import com.treatmentunit.rabbitmq.client.RmqListenerGTFS;
import com.treatmentunit.rabbitmq.client.RmqListenerPARCOURS;
import com.treatmentunit.rabbitmq.client.RmqListenerPositionAllBus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class RestServiceApplication {
	public static RmqListenerPositionAllBus rmqListenerPositionAllBus = null;
	public static RmqListenerGTFS rmqListenerGTFS = null;
	public static RmqListenerPARCOURS rmqListenerPARCOURS = null;

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		SpringApplication.run(RestServiceApplication.class, args);

		System.out.println("""
				 _____              _                        _                                                _                           _ _  \s
				|_   _|            | |                      | |     ___                                      (_)                         (_) | \s
				  | |_ __ ___  __ _| |_ _ __ ___   ___ _ __ | |_   ( _ )    _ __  _ __ ___   ___ ___  ___ ___ _ _ __   __ _   _   _ _ __  _| |_\s
				  | | '__/ _ \\/ _` | __| '_ ` _ \\ / _ \\ '_ \\| __|  / _ \\/\\ | '_ \\| '__/ _ \\ / __/ _ \\/ __/ __| | '_ \\ / _` | | | | | '_ \\| | __|
				  | | | |  __/ (_| | |_| | | | | |  __/ | | | |_  | (_>  < | |_) | | | (_) | (_|  __/\\__ \\__ \\ | | | | (_| | | |_| | | | | | |_\s
				  \\_/_|  \\___|\\__,_|\\__|_| |_| |_|\\___|_| |_|\\__|  \\___/\\/ | .__/|_|  \\___/ \\___\\___||___/___/_|_| |_|\\__, |  \\__,_|_| |_|_|\\__|
				                                                           | |       Yazid & Valere - ESIR PROJ-SI     __/ |                   \s
				                                                           |_|            2022-2023                   |___/                    \s			
				""");

		System.out.println("[*] Delaying waiting for BDD ... ");

		Thread.sleep(40000);

		System.out.println("[*] Running ... ");
		DatabaseBinding.connectToSqlSocket();
		if(args.length == 1) {
			rmqListenerPositionAllBus = new RmqListenerPositionAllBus(args[0]);
			rmqListenerGTFS = new RmqListenerGTFS(args[0]);
			rmqListenerPARCOURS = new RmqListenerPARCOURS(args[0]);
		} else if(args.length == 3) {
			rmqListenerPositionAllBus = new RmqListenerPositionAllBus(args[0], args[1], args[2]);
			rmqListenerGTFS = new RmqListenerGTFS(args[0], args[1], args[2]);
			rmqListenerPARCOURS = new RmqListenerPARCOURS(args[0], args[1], args[2]);
		} else {
			System.out.println("[!] Erreur : Nombre d'arguments incorrect !");
			System.exit(0);
		}
		rmqListenerPositionAllBus.run();
		rmqListenerGTFS.run();
		rmqListenerPARCOURS.run();
	}
}

