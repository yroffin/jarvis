package routes

import devices "org/jarvis/routes/devices"
import http "net/http"

type MyRoutesHandler struct{}

func (mh MyRoutesHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
}

/**
 * main function
 */
func Init(base string) {
	mx := http.NewServeMux()
	mx.Handle(base+"/test", devices.Get(http.Handler(&MyRoutesHandler{})))
	http.ListenAndServe(":8080", mx)
}
