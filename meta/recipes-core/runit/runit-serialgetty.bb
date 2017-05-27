# Copyright (C) 2017 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Serial terminal support for runit"

SECTION = "base utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

INHIBIT_DEFAULT_DEPS = "1"

SRC_URI = "\
           file://run \
           file://finish \
"

S = "${WORKDIR}"

SERIAL_CONSOLE ?= "115200 ttyS0"

do_install() {
	install -d ${D}${sysconfdir}/service
	tmp="${SERIAL_CONSOLES}"
	for i in $tmp
	do
		ttydev=`echo "$i" | sed -e 's/^[0-9]*\;//' -e 's/\;.*//'`
		baudrate=`echo $i | sed 's/\;.*//'`
		install -d ${D}${sysconfdir}/sv/getty-${ttydev}
		install -m 755 ${WORKDIR}/run ${D}${sysconfdir}/sv/getty-${ttydev}
		install -m 755 ${WORKDIR}/finish ${D}${sysconfdir}/sv/getty-${ttydev}
		sed -i -e s/\@BAUDRATE\@/$baudrate/g ${D}${sysconfdir}/sv/getty-${ttydev}/run
		sed -i -e s/\@BAUDRATE\@/$baudrate/g ${D}${sysconfdir}/sv/getty-${ttydev}/finish
		sed -i -e s/\@TTY\@/$ttydev/g ${D}${sysconfdir}/sv/getty-${ttydev}/run
		sed -i -e s/\@TTY\@/$ttydev/g ${D}${sysconfdir}/sv/getty-${ttydev}/finish
		ln -s ${localstatedir}/run/sv.getty-${ttydev} ${D}${sysconfdir}/sv/getty-${ttydev}/supervise
		ln -s ${sysconfdir}/sv/getty-${ttydev} ${D}${sysconfdir}/service
	done
}
# Since SERIAL_CONSOLES is likely to be set from the machine configuration
PACKAGE_ARCH = "${MACHINE_ARCH}"

RDEPENDS_${PN} = "runit"

FILES_${PN} = "${sysconfdir}"
