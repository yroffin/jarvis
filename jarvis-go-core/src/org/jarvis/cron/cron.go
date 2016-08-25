package cron

import (
	system "org/jarvis/core/system"
	logger "org/jarvis/logger"
	model "org/jarvis/model/connector"
)

import (
	"encoding/json"
	log "github.com/Sirupsen/logrus"
	"github.com/parnurzeal/gorequest"
	"github.com/robfig/cron"
	"io/ioutil"
	"strconv"
	"time"
)

/**
 * handler for connector register
 */
func handler() {
	var properties = system.GetProperties()

	/**
	 * define default value for this connector
	 */
	m := &model.ConnectorBean{}
	m.Name = "go-dio"
	m.Icon = "settings_input_antenna"
	m.Adress = "http://" + properties.Interface + ":" + strconv.Itoa(properties.Port)
	m.IsRenderer = true
	m.IsSensor = false
	m.CanAnswer = false

	mJson, _ := json.Marshal(m)

	request := gorequest.New().Timeout(2 * time.Second)
	resp, _, errs := request.
		Post(properties.ServerUrl + "/api/connectors/*?task=register").
		Send(string(mJson)).
		End()
	if errs != nil {
		logger.NewLogger().WithFields(log.Fields{"errors": errs}).Error("CRON")
	}

	if b, err := ioutil.ReadAll(resp.Body); err == nil {
		logger.NewLogger().WithFields(log.Fields{
			"body":   string(b),
			"status": resp.Status,
		}).Info("CRON")
	}
}

/**
 * init cron service
 */
func Init(cr string) int {
	/**
	 * init cron
	 */
	c := cron.New()
	c.AddFunc(cr, handler)
	c.Start()
	return 0
}
