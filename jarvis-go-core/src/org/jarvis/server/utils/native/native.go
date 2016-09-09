package native

// #cgo arm CFLAGS: -marm
// #cgo arm LDFLAGS: -lwiringPi
/*
// Par Idleman (idleman@idleman.fr - http://blog.idleman.fr)
// Licence : CC by sa
// Toutes question sur le blog ou par mail, possibilité de m'envoyer des bières via le blog
#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>
#include <sys/time.h>
#include <time.h>
#include <stdlib.h>
#include <sched.h>
// Scheduler REALTIME
void scheduler_realtime() {

	struct sched_param p;
	p.sched_priority = sched_get_priority_max(SCHED_RR);
	if (sched_setscheduler(0, SCHED_RR, &p) == -1) {
		perror("Failed to switch to realtime scheduler.");
	}
}
// Scheduler STD
void scheduler_standard() {

	struct sched_param p;
	p.sched_priority = 0;
	if (sched_setscheduler(0, SCHED_OTHER, &p) == -1) {
		perror("Failed to switch to normal scheduler.");
	}
}
// Calcul le nombre 2^chiffre indiqué, fonction utilisé par itob pour la conversion decimal/binaire
unsigned long power2(int power) {
	unsigned long integer = 1;
	int i;
	for (i = 0; i < power; i++) {
		integer *= 2;
	}
	return integer;
}
// Convertis un nombre en binaire, nécessite le nombre, et le nombre de bits souhaité en sortie (ici 26)
// Stocke le résultat dans le tableau global "bit2"
void itob(unsigned long integer, int bit2[26], int length) {
	int i;
	for (i = 0; i < length; i++) {
		if ((integer / power2(length - 1 - i)) == 1) {
			integer -= power2(length - 1 - i);
			bit2[i] = 1;
		} else
			bit2[i] = 0;
	}
}
// Convert
void itobInterruptor(unsigned long integer, int bit2Interruptor[4], int length) {
	int i;
	for (i = 0; i < length; i++) {
		if ((integer / power2(length - 1 - i)) == 1) {
			integer -= power2(length - 1 - i);
			bit2Interruptor[i] = 1;
		} else
			bit2Interruptor[i] = 0;
	}
}
// Envois d'une pulsation (passage de l'etat haut a l'etat bas)
// 1 = 310µs haut puis 1340µs bas
// 0 = 310µs haut puis 310µs bas
void sendBit(int pin, int b) {
	if (b) {
		digitalWrite(pin, 1);
		delayMicroseconds(310);   //275 orinally, but tweaked.
		digitalWrite(pin, 0);
		delayMicroseconds(1340);  //1225 orinally, but tweaked.
	} else {
		digitalWrite(pin, 1);
		delayMicroseconds(310);   //275 orinally, but tweaked.
		digitalWrite(pin, 0);
		delayMicroseconds(310);   //275 orinally, but tweaked.
	}
}
// Envoie d'une paire de pulsation radio qui definissent 1 bit réel : 0 =01 et 1 =10
// c'est le codage de manchester qui necessite ce petit bouzin, ceci permet entre autres de dissocier les données des parasites
void sendPair(int pin, int b) {
	if (b) {
		sendBit(pin, 1);
		sendBit(pin, 0);
	} else {
		sendBit(pin, 1);
		sendBit(pin, 0);
	}
}
// Fonction d'envois du signal
// recoit en parametre un booleen définissant l'arret ou la marche du matos (true = on, false = off)
void transmit(int pin, int bit2[26], int bit2Interruptor[4], int blnOn) {
	int i;

	// Sequence de verrou anoncant le départ du signal au recepeteur
	digitalWrite(pin, 1);
	delayMicroseconds(275); // un bit de bruit avant de commencer pour remettre les delais du recepteur a 0
	digitalWrite(pin, 0);
	delayMicroseconds(9900);     // premier verrou de 9900µs
	digitalWrite(pin, 1);   // high again
	delayMicroseconds(275);      // attente de 275µs entre les deux verrous
	digitalWrite(pin, 0);    // second verrou de 2675µs
	delayMicroseconds(2675);
	digitalWrite(pin, 1); // On reviens en état haut pour bien couper les verrous des données

	// Envoie du code emetteur (272946 = 1000010101000110010  en binaire)
	for (i = 0; i < 26; i++) {
		sendPair(pin, bit2[i]);
	}

	// Envoie du bit définissant si c'est une commande de groupe ou non (26em bit)
	sendPair(pin, 0);

	// Envoie du bit définissant si c'est allumé ou eteint 27em bit)
	sendPair(pin, blnOn);

	// Envoie des 4 derniers bits, qui représentent le code interrupteur, ici 0 (encode sur 4 bit donc 0000)
	// nb: sur  les télécommandes officielle chacon, les interrupteurs sont logiquement nommés de 0 à x
	// interrupteur 1 = 0 (donc 0000) , interrupteur 2 = 1 (1000) , interrupteur 3 = 2 (0100) etc...
	for (i = 0; i < 4; i++) {
		if (bit2Interruptor[i] == 0) {
			sendPair(pin, 0);
		} else {
			sendPair(pin, 1);
		}
	}

	digitalWrite(pin, 1);   // coupure données, verrou
	delayMicroseconds(275);      // attendre 275µs
	digitalWrite(pin, 0); // verrou 2 de 2675µs pour signaler la fermeture du signal

}
// Send ON
int dioOn(int pin, int sender, int interruptor) {
	int bit2[26];
	int bit2Interruptor[4];

	scheduler_realtime();

	pinMode(pin, 1);
	itob(sender, bit2, 26);
	itobInterruptor(interruptor, bit2Interruptor, 4);

	int i;
	for (i = 0; i < 5; i++) {
		transmit(pin, bit2, bit2Interruptor, 1);
		delay(10);
	}

	scheduler_standard();

	return 0;
}
// Send OFF
int dioOff(int pin, int sender, int interruptor) {
	int bit2[26];
	int bit2Interruptor[4];

	scheduler_realtime();

	pinMode(pin, 1);
	itob(sender, bit2, 26);
	itobInterruptor(interruptor, bit2Interruptor, 4);

	int i;
	for (i = 0; i < 5; i++) {
		transmit(pin, bit2, bit2Interruptor, 0);
		delay(10);
	}

	scheduler_standard();

	return 0;
}
// Init wiringPi library
int initWiringPi() {

	if (setuid(0)) {
		perror("Failed to call setuid.");
		return 1;
	}

	if (wiringPiSetup() == -1) {
		return -1;
	}

	return 0;
}
*/
import "C"

func InitWiringPi() int {
	return int(C.initWiringPi())
}

func DioOn(pin int, sender int, interruptor int) int {
	return int(C.dioOn(C.int(pin), C.int(sender), C.int(interruptor)))
}

/**
 * push OFF
 */
func DioOff(pin int, sender int, interruptor int) int {
	return int(C.dioOff(C.int(pin), C.int(sender), C.int(interruptor)))
}
