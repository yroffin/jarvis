package native

// #cgo LDFLAGS: -lwiringPi
// #include "native.h"
import "C"

func InitWiringPi() int {
	return int(C.initWiringPi())
}

func DioOn(pin int, sender int, interruptor int) int {
	return int(C.dioOn(C.int(pin), C.int(sender), C.int(interruptor)))
}

func DioOff(pin int, sender int, interruptor int) int {
	return int(C.dioOff(C.int(pin), C.int(sender), C.int(interruptor)))
}
