
WHERE=$(PWD)
WIRINGPI_ARMEL=${WHERE}/armel/wiringPi
WIRINGPI_ARMHF=${WHERE}/armhf/wiringPi
MODULE_ARMEL=${WHERE}/jarvis-rest-module-0.0.1-SNAPSHOT.armel
MODULE_ARMHF=${WHERE}/jarvis-rest-module-0.0.1-SNAPSHOT.armhf

all: clean ${MODULE_ARMEL} ${MODULE_ARMHF}

clean:
	rm -rf ${WHERE}/armel
	rm -rf ${WHERE}/armhf
	rm -f ${MODULE_ARMEL}
	rm -f ${MODULE_ARMHF}

${MODULE_ARMEL}:
	# wiringPI
	git clone git://git.drogon.net/wiringPi ${WHERE}/armel
	cd ${WIRINGPI_ARMEL} && sed 's!gcc!arm-linux-gnueabi-gcc!g' Makefile > Makefile.tmp && mv -f Makefile.tmp Makefile
	cd ${WIRINGPI_ARMEL} && sed 's!PIC!PIC -march=armv6j -mfloat-abi=soft!g' Makefile > Makefile.tmp && mv -f Makefile.tmp Makefile
	cd ${WIRINGPI_ARMEL} && make all
	sudo mv -f ${WIRINGPI_ARMEL}/libwiringPi.so.2.32 /usr/lib/libwiringPi.so
	readelf -A /usr/lib/libwiringPi.so
	# module
	GOPATH=${GOIMPORT}:${WHERE} CC=arm-linux-gnueabi-gcc GOOS=linux GOARCH=arm GOARM=6 CGO_ENABLED=1 CGO_CFLAGS="-march=armv6j -mfloat-abi=soft" CGO_LDFLAGS="-lwiringPi" go install -installsuffix armel ./...
	mv -f bin/linux_arm/main jarvis-rest-module-0.0.1-SNAPSHOT.armel

${MODULE_ARMHF}:
	# wiringPI
	git clone git://git.drogon.net/wiringPi ${WHERE}/armhf
	cd ${WIRINGPI_ARMHF} && sed 's!gcc!arm-linux-gnueabihf-gcc!g' Makefile > Makefile.tmp && mv -f Makefile.tmp Makefile
	cd ${WIRINGPI_ARMHF} && sed 's!PIC!PIC -march=armv7 -mfloat-abi=hard!g' Makefile > Makefile.tmp && mv -f Makefile.tmp Makefile
	cd ${WIRINGPI_ARMHF} && make all
	sudo mv -f ${WIRINGPI_ARMHF}/libwiringPi.so.2.32 /usr/lib/libwiringPi.so
	readelf -A /usr/lib/libwiringPi.so
	# module
	GOPATH=${GOIMPORT}:${WHERE} CC=arm-linux-gnueabihf-gcc GOOS=linux GOARCH=arm GOARM=7 CGO_ENABLED=1 CGO_CFLAGS="" CGO_LDFLAGS="-lwiringPi" go install -installsuffix armhf ./...
	mv -f bin/linux_arm/main jarvis-rest-module-0.0.1-SNAPSHOT.armhf

