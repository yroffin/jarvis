
all: always bin/main
always:
	@rm -f bin/main
bin/main: always
	rm -rf pkg/*
	GOPATH=${PWD}/tmp go get -v github.com/Sirupsen/logrus
	GOPATH=${PWD}/tmp go get -v gopkg.in/gemnasium/logrus-airbrake-hook.v2
	GOPATH=${PWD}/tmp go get -v github.com/magiconair/properties
	GOPATH=${PWD}/tmp:${PWD} CFLAGS=-DWIRINGPI go install -v -gcflags "-N -l" ./...
	mv bin/main bin/jarvis-rest-module-dio
