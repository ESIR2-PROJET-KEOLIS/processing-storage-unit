package com.treatmentunit.restservice;

import com.rabbitmq.client.Channel;
import com.treatmentunit.rabbitmq.client.RmqListener;
import com.treatmentunit.rabbitmq.client.RmqPublisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class RestServiceApplication {
	public static RmqListener rmqListener = null;

	public static void main(String[] args) throws IOException, TimeoutException {
		SpringApplication.run(RestServiceApplication.class, args);

		System.out.println("""
				 _____              _                        _                                                _                           _ _  \s
				|_   _|            | |                      | |     ___                                      (_)                         (_) | \s
				  | |_ __ ___  __ _| |_ _ __ ___   ___ _ __ | |_   ( _ )    _ __  _ __ ___   ___ ___  ___ ___ _ _ __   __ _   _   _ _ __  _| |_\s
				  | | '__/ _ \\/ _` | __| '_ ` _ \\ / _ \\ '_ \\| __|  / _ \\/\\ | '_ \\| '__/ _ \\ / __/ _ \\/ __/ __| | '_ \\ / _` | | | | | '_ \\| | __|
				  | | | |  __/ (_| | |_| | | | | |  __/ | | | |_  | (_>  < | |_) | | | (_) | (_|  __/\\__ \\__ \\ | | | | (_| | | |_| | | | | | |_\s
				  \\_/_|  \\___|\\__,_|\\__|_| |_| |_|\\___|_| |_|\\__|  \\___/\\/ | .__/|_|  \\___/ \\___\\___||___/___/_|_| |_|\\__, |  \\__,_|_| |_|_|\\__|
				                                                           | |       Yazid - ESIR PROJ-SI              __/ |                   \s
				                                                           |_|            2022-2023                   |___/                    \s			
				""");

		System.out.println("[*] Running ... ");
		if(args.length == 1) {
			rmqListener = new RmqListener(args[0]);
		} else if(args.length == 3) {
			rmqListener = new RmqListener(args[0], args[1], args[2]);
		} else {
			System.out.println("[!] Erreur : Nombre d'arguments incorrect !");
			System.exit(0);
		}
		rmqListener.run();
	}
}

