#include <sys/types.h>
#include <unistd.h>
#include <wiringPi.h>
#include <stdio.h>
#include <sys/time.h>
#include <time.h>
#include <stdlib.h>
#include <sched.h>

#ifndef WIRINGPI
int  setuid      		(int uid);
#endif

/*
 Par Idleman (idleman@idleman.fr - http://blog.idleman.fr)
 Licence : CC by sa
 Toutes question sur le blog ou par mail, possibilité de m'envoyer des bières via le blog
 */

typedef int bool;
typedef char* string;
#define true 1
#define false 0

int pin;
bool bit2[26] = { };              // 26 bit Identifiant emetteur
bool bit2Interruptor[4] = { };
int interruptor;
int sender;
string onoff;

void scheduler_realtime() {

	struct sched_param p;
	p.sched_priority = sched_get_priority_max(SCHED_RR);
	if (sched_setscheduler(0, SCHED_RR, &p) == -1) {
		perror("Failed to switch to realtime scheduler.");
	}
}

void scheduler_standard() {

	struct sched_param p;
	p.sched_priority = 0;
	if (sched_setscheduler(0, SCHED_OTHER, &p) == -1) {
		perror("Failed to switch to normal scheduler.");
	}
}

//Envois d'une pulsation (passage de l'etat haut a l'etat bas)
//1 = 310µs haut puis 1340µs bas
//0 = 310µs haut puis 310µs bas
void sendBit(bool b) {
	if (b) {
		digitalWrite(pin, HIGH);
		delayMicroseconds(310);   //275 orinally, but tweaked.
		digitalWrite(pin, LOW);
		delayMicroseconds(1340);  //1225 orinally, but tweaked.
	} else {
		digitalWrite(pin, HIGH);
		delayMicroseconds(310);   //275 orinally, but tweaked.
		digitalWrite(pin, LOW);
		delayMicroseconds(310);   //275 orinally, but tweaked.
	}
}

//Calcul le nombre 2^chiffre indiqué, fonction utilisé par itob pour la conversion decimal/binaire
unsigned long power2(int power) {
	unsigned long integer = 1;
	int i;
	for (i = 0; i < power; i++) {
		integer *= 2;
	}
	return integer;
}

//Convertis un nombre en binaire, nécessite le nombre, et le nombre de bits souhaité en sortie (ici 26)
// Stocke le résultat dans le tableau global "bit2"
void itob(unsigned long integer, int length) {
	int i;
	for (i = 0; i < length; i++) {
		if ((integer / power2(length - 1 - i)) == 1) {
			integer -= power2(length - 1 - i);
			bit2[i] = 1;
		} else
			bit2[i] = 0;
	}
}

void itobInterruptor(unsigned long integer, int length) {
	int i;
	for (i = 0; i < length; i++) {
		if ((integer / power2(length - 1 - i)) == 1) {
			integer -= power2(length - 1 - i);
			bit2Interruptor[i] = 1;
		} else
			bit2Interruptor[i] = 0;
	}
}

//Envoie d'une paire de pulsation radio qui definissent 1 bit réel : 0 =01 et 1 =10
//c'est le codage de manchester qui necessite ce petit bouzin, ceci permet entre autres de dissocier les données des parasites
void sendPair(bool b) {
	if (b) {
		sendBit(true);
		sendBit(false);
	} else {
		sendBit(false);
		sendBit(true);
	}
}

//Fonction d'envois du signal
//recoit en parametre un booleen définissant l'arret ou la marche du matos (true = on, false = off)
void transmit(int blnOn) {
	int i;

	// Sequence de verrou anoncant le départ du signal au recepeteur
	digitalWrite(pin, HIGH);
	delayMicroseconds(275); // un bit de bruit avant de commencer pour remettre les delais du recepteur a 0
	digitalWrite(pin, LOW);
	delayMicroseconds(9900);     // premier verrou de 9900µs
	digitalWrite(pin, HIGH);   // high again
	delayMicroseconds(275);      // attente de 275µs entre les deux verrous
	digitalWrite(pin, LOW);    // second verrou de 2675µs
	delayMicroseconds(2675);
	digitalWrite(pin, HIGH); // On reviens en état haut pour bien couper les verrous des données

	// Envoie du code emetteur (272946 = 1000010101000110010  en binaire)
	for (i = 0; i < 26; i++) {
		sendPair(bit2[i]);
	}

	// Envoie du bit définissant si c'est une commande de groupe ou non (26em bit)
	sendPair(false);

	// Envoie du bit définissant si c'est allumé ou eteint 27em bit)
	sendPair(blnOn);

	// Envoie des 4 derniers bits, qui représentent le code interrupteur, ici 0 (encode sur 4 bit donc 0000)
	// nb: sur  les télécommandes officielle chacon, les interrupteurs sont logiquement nommés de 0 à x
	// interrupteur 1 = 0 (donc 0000) , interrupteur 2 = 1 (1000) , interrupteur 3 = 2 (0100) etc...
	for (i = 0; i < 4; i++) {
		if (bit2Interruptor[i] == 0) {
			sendPair(false);
		} else {
			sendPair(true);
		}
	}

	digitalWrite(pin, HIGH);   // coupure données, verrou
	delayMicroseconds(275);      // attendre 275µs
	digitalWrite(pin, LOW); // verrou 2 de 2675µs pour signaler la fermeture du signal

}

/**
 * init libray
 */
int initWiringPi() {

	if (setuid(0)) {
		perror("setuid");
		return 1;

	}

	/**
	 * abort init if no wiringPiSetup ok
	 */
	if (wiringPiSetup() == -1) {
		return -1;
	}

	return 0;
}

/**
 * send on
 */
int dioOn(int pin, int sender, int interruptor) {

	scheduler_realtime();

	/**
	 * fix pin mode in output mode
	 */
	pinMode(pin, OUTPUT);

	/**
	 * convert sender and interruptor
	 */
	itob(sender, 26);
	itobInterruptor(interruptor, 4);

	int i;
	for (i = 0; i < 5; i++) {
		transmit(true);
		delay(10);
	}

	scheduler_standard();

	return 0;
}

/**
 * send off
 */
int dioOff(int pin, int sender, int interruptor) {

	scheduler_realtime();

	/**
	 * fix pin mode in output mode
	 */
	pinMode(pin, OUTPUT);

	/**
	 * convert sender and interruptor to binary
	 */
	itob(sender, 26);
	itobInterruptor(interruptor, 4);

	int i;
	for (i = 0; i < 5; i++) {
		/**
		 * send off
		 */
		transmit(false);
		delay(10);
	}

	scheduler_standard();

	return 0;
}
