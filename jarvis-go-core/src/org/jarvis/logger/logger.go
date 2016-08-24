package logger

import (
	log "github.com/Sirupsen/logrus"
)

var Log *log.Logger

func NewLogger() *log.Logger {
	if Log != nil {
		return Log
	}

	Log = log.New()
	Log.Formatter = new(log.JSONFormatter)

	return Log
}
