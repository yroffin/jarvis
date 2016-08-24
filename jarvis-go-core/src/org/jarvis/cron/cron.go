package cron

import "github.com/robfig/cron"

import (
	log "github.com/Sirupsen/logrus"
	logger "org/jarvis/logger"
)

/**
 * init cron service
 */
func Init(cr string) int {
	c := cron.New()
	c.AddFunc(cr, func() {
		logger.NewLogger().WithFields(log.Fields{}).Info("CRON")
	})
	c.Start()
	return 0
}
