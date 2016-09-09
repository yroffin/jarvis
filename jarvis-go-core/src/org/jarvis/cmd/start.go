package cmd

import (
	"github.com/richardlt/jarvis/jarvis-go-core/src/org/jarvis/server"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

var startCmd = &cobra.Command{
	Use:   "start",
	Short: "Start the jarvis core app",
	Long:  "",
	Run: func(cmd *cobra.Command, args []string) {
		server.Start()
	},
}

func init() {
	viper.AutomaticEnv()

	startFlags := startCmd.Flags()

	startFlags.Int("jarvis.core.port", 8080, "set the listening jarvis core port")
	viper.BindPFlag("jarvis.core.port", startFlags.Lookup("jarvis.core.port"))

	startFlags.Int("jarvis.module.port", 7000, "set the listening jarvis module port")
	viper.BindPFlag("jarvis.module.port", startFlags.Lookup("jarvis.module.port"))

	startFlags.String("jarvis.module.interface", "0.0.0.0", "set the listening jarvis module interface")
	viper.BindPFlag("jarvis.module.interface", startFlags.Lookup("jarvis.module.interface"))

	startFlags.String("jarvis.module.name", "module", "set the listening jarvis module name")
	viper.BindPFlag("jarvis.module.name", startFlags.Lookup("jarvis.module.name"))

	startFlags.String("jarvis.server.url", "http://0.0.0.0:8082", "set the listening jarvis server url")
	viper.BindPFlag("jarvis.server.url", startFlags.Lookup("jarvis.server.url"))

	RootCmd.AddCommand(startCmd)
}
