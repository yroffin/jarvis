package logger

import (
	log "github.com/Sirupsen/logrus"
)

// Log : log
var Log *log.Logger

// NewLogger : new logger
func NewLogger() *log.Logger {
	if Log != nil {
		return Log
	}

	Log = log.New()
	Log.Formatter = new(log.JSONFormatter)

	return Log
}
