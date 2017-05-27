# Copyright (C) 2017 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "A UNIX init scheme with service supervision"
HOMEPAGE = "http://smarden.org/runit/"
LICENSE = "BSD-3-Clause"
SECTION = "base"

LIC_FILES_CHKSUM = "file://package/COPYING;md5=c9e8a560732fc8b860b6a91341cc603b"

inherit update-alternatives

SRC_URI = "http://smarden.org/${BPN}/${BP}.tar.gz \
           file://0001-default-directory-for-services-on-Debian-is-etc-servi.diff;striplevel=2 \
           file://0002-support-etc-runit-nosync-file-to-make-sync-on-shutdow.diff;striplevel=2 \
           file://0003-utmpset.c-mixes-int32_t-and-time_t.diff;striplevel=2 \
           file://0004-src-Makefile-don-t-use-static-to-link-runit-runit-ini.diff;striplevel=2 \
           file://0005-patch-etc-runit-2-for-FHS.patch;striplevel=2 \
           file://0006-make-buildsystem-respect-CFLAGS.patch;striplevel=2 \
           file://0007-move-communication-files.patch;striplevel=2 \
           file://0008-emulate-sysv-runlevel-5.patch;striplevel=2 \
           file://0009-fix-error-in-manpage.patch;striplevel=2 \
           file://cross.patch \
"

SRC_URI[md5sum] = "6c985fbfe3a34608eb3c53dc719172c4"
SRC_URI[sha256sum] = "6fd0160cb0cf1207de4e66754b6d39750cff14bb0aa66ab49490992c0c47ba18"

S = "${WORKDIR}/admin/${BPN}-${PV}"

do_compile() {
	cd ${S}/src
	sed -e 's,sbin/runit,usr/bin/runit,g' -i ${S}/src/runit.h
	echo "$CC -D_GNU_SOURCE $CFLAGS" >conf-cc
	echo "$CC $LDFLAGS -Wl,-z -Wl,noexecstack" >conf-ld
	# change type short to gid_t for getgroups(2) and setgroups(2)
	sed -i -e 's:short x\[4\];$:gid_t x[4];:' ${S}/src/chkshsgr.c
	oe_runmake

}

do_install() {
	cd ${S}/src
	install -d ${D}${bindir}
	for f in chpst runit runit-init runsv runsvchdir runsvdir \
		sv svlogd utmpset; do
		install -m 0755 $f ${D}${bindir}
	done
	install -d ${D}${sysconfdir}/runit
	for f in 1 2 3 ctrlaltdel; do
		install -m 0755 ${S}/etc/debian/$f ${D}${sysconfdir}/runit
	done
	sed -e 's,rmnologin,rmnologin.sh,g' -i ${D}${sysconfdir}/runit/1
	ln -s ${localstatedir}/run/runit.stopit ${D}${sysconfdir}/runit/stopit
}

ALTERNATIVE_${PN} = "init"
ALTERNATIVE_TARGET[init] = "${bindir}/runit-init"
ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
ALTERNATIVE_PRIORITY[init] ?= "300"
