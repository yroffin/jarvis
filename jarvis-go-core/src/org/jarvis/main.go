package main

import (
	"fmt"
	"os"

	"github.com/richardlt/jarvis/jarvis-go-core/src/org/jarvis/cmd"
)

// main function
func main() {

	if err := cmd.RootCmd.Execute(); err != nil {
		fmt.Println(err)
		os.Exit(-1)
	}

}
