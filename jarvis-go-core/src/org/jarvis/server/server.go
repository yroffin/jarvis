package server

import (
	"github.com/richardlt/jarvis/jarvis-go-core/src/org/jarvis/server/utils/cron"
	"github.com/richardlt/jarvis/jarvis-go-core/src/org/jarvis/server/utils/native"
	"github.com/spf13/viper"

	"github.com/labstack/echo"
	"github.com/labstack/echo/engine/standard"
	"github.com/labstack/echo/middleware"
)

// Start : start the jarvis server
func Start() {
	e := echo.New()
	e.Use(middleware.Logger())
	e.Use(middleware.Recover())

	api := e.Group("/api")
	{ // routes for /api
		dio := api.Group("/dio")
		{ // routes for /api/dio
			dio.Post("", dio.HandlePost)
		}
	}

	// init wiringPi library
	native.InitWiringPi()

	// init cron
	cron.Init("@every 60s")

	port := viper.GetString("jarvis.core.port")
	e.Run(standard.New(":" + port))
}
