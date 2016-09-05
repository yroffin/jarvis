package main

import (
	fmt "fmt"
	cron "org/jarvis/cron"
	routes "org/jarvis/routes"
)

/**
 * main function
 */
func main() {
	fmt.Printf("Start module")
	cron.Init("@every 60s")
	routes.Init("/api")
}
