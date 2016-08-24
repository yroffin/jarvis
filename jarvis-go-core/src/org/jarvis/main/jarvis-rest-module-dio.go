package main

import (
	cron "org/jarvis/cron"
	routes "org/jarvis/routes"
)

/**
 * main function
 */
func main() {
	cron.Init("* * * * * *")
	routes.Init("/api")
}
