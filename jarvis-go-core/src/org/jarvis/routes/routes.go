package routes

import "net/http"
import "org/jarvis/routes/devices"

type MyRoutesHandler struct{}

func (mh MyRoutesHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
}

/**
 * main function
 */
func Init(base string) {
	mx := http.NewServeMux()
	mx.Handle(base + "/test", devices.Get(http.Handler(&MyRoutesHandler{})))
	http.ListenAndServe(":8080", mx)
}
