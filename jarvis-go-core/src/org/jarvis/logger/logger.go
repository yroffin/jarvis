package logger

import (
	log "github.com/Sirupsen/logrus"
	"github.com/rifflock/lfshook"
)

var Log *log.Logger

func NewLogger() *log.Logger {
	if Log != nil {
		return Log
	}

	Log = log.New()
	Log.Formatter = new(log.JSONFormatter)
	Log.Hooks.Add(lfshook.NewHook(lfshook.PathMap{
		log.InfoLevel:  "module.log",
		log.ErrorLevel: "module.log",
	}))
	return Log
}
