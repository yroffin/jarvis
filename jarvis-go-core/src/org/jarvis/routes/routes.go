package routes

import devices "org/jarvis/routes/devices"
import http "net/http"
import fmt "fmt"
import native "org/jarvis/native"

type MyRoutesHandler struct{}

func (mh MyRoutesHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
}

/**
 * main function
 */
func Init(base string) {
	mx := http.NewServeMux()

	mx.Handle(base+"/dio", devices.PostHandler(http.Handler(&MyRoutesHandler{})))

	/**
	 * init wiringPi library
	 */
	native.InitWiringPi()

	fmt.Printf("ListenAndServe on %s\n", base+"/dio")
	http.ListenAndServe(":8080", mx)
}
