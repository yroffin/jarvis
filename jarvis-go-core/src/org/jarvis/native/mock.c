#include <sys/types.h>
#include <unistd.h>
#include <wiringPi.h>
#include <stdio.h>
#include <sys/time.h>
#include <time.h>
#include <stdlib.h>
#include <sched.h>

#define NOMOCK

#ifdef MOCK
void  delay             (unsigned int howLong) {}
void  delayMicroseconds (unsigned int howLong) {}
unsigned int millis            (void) {}
unsigned int micros            (void) {}
void digitalWrite        (int pin, int value) {
	fprintf(stderr, "digitalWrite(%d,%d)\n", pin, value);
	fflush(stderr);
}
int  wiringPiSetup       (void) {return 0;}
void pinMode             (int pin, int mode) {}
int  setuid       (int uid) {return 0;}
#endif
