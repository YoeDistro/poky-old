# Copyright (C) 2017 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Additional runit scripts from void distro"
HOMEPAGE = "https://github.com/voidlinux/void-runit"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://README.md;beginline=41;endline=48;md5=f2f8535b84b11359cc7757b009cfd646"
SECTION = "base"

PV = "20160826+git${SRCPV}"

SRCREV = "fd2a983c3e466a408d68169812fe388f8a3927ce"
SRC_URI = "git://github.com/voidlinux/void-runit"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "PREFIX=${exec_prefix}"

do_install() {
	oe_runmake DESTDIR=${D} install
	for f in init shutdown halt reboot poweroff
	do
		ln -sr ${bindir}/$f ${D}${base_bindir}/$f
	done
}

pkg_postinst_${PN} () {
        # Enable default services:
        #       - agetty-tty[1-4] (default)
        #       - udevd (default)
        #       - sulogin (single)
        mkdir -p $D/etc/runit/runsvdir/single
        ln -sf /etc/sv/sulogin $D/etc/runit/runsvdir/single

        mkdir -p $D/etc/runit/runsvdir/default
        if [ ! -e $D/etc/runit/runsvdir/current ]; then
                ln -sf default $D/etc/runit/runsvdir/current
        fi
        [ -e $D/etc/sv/udevd/run ] && ln -sf /etc/sv/udevd $D/etc/runit/runsvdir/default

}

RDEPENDS_${PN} = "runit"

PACKAGES =+ "${PN}-dracut"

FILES_${PN}-dracut = "${libdir}/dracut"
