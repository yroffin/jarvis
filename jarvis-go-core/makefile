
all: always bin/main
always:
	@rm -f bin/main
bin/main: always
	rm -rf pkg/*
	GOPATH=${PWD}/tmp go get -v github.com/Sirupsen/logrus
	GOPATH=${PWD}/tmp go get -v gopkg.in/gemnasium/logrus-airbrake-hook.v2
	GOPATH=${PWD}/tmp go get -v github.com/magiconair/properties
	GOPATH=${PWD}/tmp go get -v github.com/parnurzeal/gorequest
	GOPATH=${PWD}/tmp go get -v github.com/robfig/cron
	GOPATH=${PWD}/tmp go get -v golang.org/x/text/transform
	GOPATH=${PWD}/tmp go get -v golang.org/x/crypto/ssh/terminal
	GOPATH=${PWD}/tmp:${PWD} CFLAGS=-DWIRINGPI go install -v -gcflags "-N -l" ./...
	mv bin/main bin/jarvis-rest-module-dio
