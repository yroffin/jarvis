package syscall

// #cgo amd64 386 CFLAGS: -DMOCK
// #cgo arm LDFLAGS:
// #include <sched.h>
// void scheduler_realtime() {
// struct sched_param p;
// p.sched_priority = sched_get_priority_max(SCHED_RR);
// if(sched_setscheduler(0, SCHED_RR, &p) == -1) {
// }
//}
// #ifdef WIRINGPI
// #else
// void  delay             	(unsigned int howLong) {}
// void  delayMicroseconds 	(unsigned int howLong) {}
// unsigned int millis      (void) {}
// unsigned int micros      (void) {}
// void digitalWrite        (int pin, int value) {}
// int  wiringPiSetup       (void) {return 0;}
// void pinMode             (int pin, int mode) {}
// int  setuid      		(int uid) {return 0;}
// #endif
import "C"

//import sys "golang.org/x/sys/unix"

/**
 * init the wiringPi library
 */
func InitWiringPi() int {
	if int(C.wiringPiSetup()) == -1 {
		return -1
	} else {
		return 0
	}
}

func scheduler_realtime() {
	C.scheduler_realtime()
}

func scheduler_standard() {
	//struct sched_param p;
	//p.sched_priority = 0;
	// if sys.sched_setscheduler(0, SCHED_OTHER, &p) == -1 {
	//	perror("Failed to switch to normal scheduler.")
	//}
}

func DioOn(pin int, sender int, interruptor int) int {
	return int(0)
}

func DioOff(pin int, sender int, interruptor int) int {
	return int(0)
}

/*
func InitWiringPi() int {
	return int(C.initWiringPi())
}

func DioOn(pin int, sender int, interruptor int) int {
	return int(C.dioOn(C.int(pin), C.int(sender), C.int(interruptor)))
}

func DioOff(pin int, sender int, interruptor int) int {
	return int(C.dioOff(C.int(pin), C.int(sender), C.int(interruptor)))
}
*/
