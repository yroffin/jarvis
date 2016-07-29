package main
 
import "net/http"
 
type MyHandler struct{}
 
func (mh MyHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
    w.Write([]byte("<p>An Easy Handler</p>"))
}
 
func SayHelloHandler(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        w.Write([]byte("<h1>Hello</h1>"))
        next.ServeHTTP(w, r)
    })
}
 
func main() {
    mx := http.NewServeMux()
    mx.Handle("/test", SayHelloHandler(http.Handler(&MyHandler{})))
    http.ListenAndServe(":8080", mx)
}
