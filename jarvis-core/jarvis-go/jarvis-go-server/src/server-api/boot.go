package boot

import (
	"log"
	"net/http"
    "os"
    "io"
    "io/ioutil"

	"github.com/emicklei/go-restful"
	"github.com/emicklei/go-restful/swagger"

	"server-api/models"
)

func check(e error) {
    if e != nil {
        panic(e)
    }
}

func HandleIndex(w http.ResponseWriter, r *http.Request) {
	log.Printf(r.RequestURI)
	dat, err := ioutil.ReadFile("resources/public"+r.RequestURI)
	check(err)
    io.WriteString(w, string(dat))
}

func Boot() {
	// to see what happens in the package, uncomment the following
	restful.TraceLogger(log.New(os.Stdout, "[restful] ", log.LstdFlags|log.Lshortfile))

	wsContainer := restful.NewContainer()
	u := user.UserResource{map[string]user.User{}}
	u.Register(wsContainer)

	// Optionally, you can install the Swagger Service which provides a nice Web UI on your REST API
	// You need to download the Swagger HTML5 assets and change the FilePath location in the config below.
	// Open http://localhost:8080/apidocs and enter http://localhost:8080/apidocs.json in the api input field.
	config := swagger.Config{
		WebServices:    wsContainer.RegisteredWebServices(), // you control what services are visible
		WebServicesUrl: "http://localhost:8080",
		ApiPath:        "/apidocs.json",

		// Optionally, specifiy where the UI is located
		SwaggerPath:     "/swagger",
		SwaggerFilePath: "resources/public/swagger"}
	swagger.RegisterSwaggerService(config, wsContainer)

	// scan static files
	log.Printf("[http/ui] %v%v is mapped to folder %v", config.WebServicesUrl, "/ui", "resources/public/ui")
	wsContainer.Handle("/ui", http.StripPrefix("/ui", http.FileServer(http.Dir("resources/public/ui"))))

	log.Printf("start listening on localhost:8080")
	server := &http.Server{Addr: ":8080", Handler: wsContainer}

   // public views
	log.Fatal(server.ListenAndServe())
}
