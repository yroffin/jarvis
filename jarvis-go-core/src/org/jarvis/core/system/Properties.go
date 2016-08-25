package system

import (
	log "github.com/Sirupsen/logrus"
	"github.com/magiconair/properties"
)

import (
	logger "org/jarvis/logger"
)

// configuration
type ConfigBean struct {
	Port      int    `properties:"jarvis.module.port,default=7000"`
	Interface string `properties:"jarvis.module.interface,default=0.0.0.0"`
	Name      string `properties:"jarvis.module.name,default=module"`
	ServerUrl string `properties:"jarvis.server.url,default=http://0.0.0.0:8082"`
}

var Properties *ConfigBean

/**
 * get properties
 */
func GetProperties() *ConfigBean {
	if Properties == nil {
		p := properties.MustLoadFile("module.properties", properties.UTF8)

		var cfg ConfigBean
		if err := p.Decode(&cfg); err != nil {
			logger.NewLogger().WithFields(log.Fields{"errors": err}).Error("PROPS")
		}

		Properties = &cfg
	}
	return Properties
}
