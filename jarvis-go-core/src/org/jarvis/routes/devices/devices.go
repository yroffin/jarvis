package devices

import "net/http"

func Get(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        w.Write([]byte("<h1>Hello</h1>"))
        next.ServeHTTP(w, r)
    })
}

