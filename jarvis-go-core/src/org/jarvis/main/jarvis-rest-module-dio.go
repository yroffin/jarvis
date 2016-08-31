package main

import (
	cron "org/jarvis/cron"
	routes "org/jarvis/routes"
)

/**
 * main function
 */
func main() {
	cron.Init("@every 60s")
	routes.Init("/api")
}
