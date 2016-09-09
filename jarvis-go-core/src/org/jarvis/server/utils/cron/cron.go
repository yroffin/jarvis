package cron

import (
	"encoding/json"
	"io/ioutil"
	"time"

	"github.com/parnurzeal/gorequest"
	"github.com/richardlt/jarvis/jarvis-go-core/src/org/jarvis/server/types"
	"github.com/richardlt/jarvis/jarvis-go-core/src/org/jarvis/server/utils/logger"
	"github.com/robfig/cron"
	"github.com/spf13/viper"
)

// handler : handler for connector register
func handler() {

	// define default value for this connector
	m := &types.Connector{
		Name:       "go-dio",
		Icon:       "settings_input_antenna",
		Adress:     "http://" + viper.GetString("jarvis.module.interface") + ":" + viper.GetString("jarvis.module.port"),
		IsRenderer: true,
		IsSensor:   false,
		CanAnswer:  false,
	}

	mJSON, _ := json.Marshal(m)

	request := gorequest.New().Timeout(2 * time.Second)
	resp, _, errs := request.
		Post(viper.GetString("jarvis.server.url") + "/api/connectors/*?task=register").
		Send(string(mJSON)).
		End()
	if errs != nil {
		logger.NewLogger().WithFields(log.Fields{
			"errors": errs,
		}).Info("CRON")
	}

	if b, err := ioutil.ReadAll(resp.Body); err == nil {
		logger.NewLogger().WithFields(log.Fields{
			"body":   string(b),
			"status": resp.Status,
		}).Info("CRON")
	}
}

// Init : init cron service
func Init(cr string) int {
	// first call
	handler()
	// init cron
	c := cron.New()
	c.AddFunc(cr, handler)
	c.Start()
	return 0
}
