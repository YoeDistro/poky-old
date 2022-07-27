require rust-target.inc
require rust-source.inc
require rust-snapshot.inc

INSANE_SKIP:${PN}:class-native = "already-stripped"
FILES:${PN} += "${libdir}/rustlib"
FILES:${PN} += "${libdir}/*.so"
FILES:${PN}-dev = ""

do_compile () {
    rust_runx build --stage 2
}

do_compile:append:class-target () {
    rust_runx build --stage 2 src/tools/clippy
    rust_runx build --stage 2 src/tools/rustfmt
}

do_compile:append:class-nativesdk () {
    rust_runx build --stage 2 src/tools/clippy
    rust_runx build --stage 2 src/tools/rustfmt
}

ALLOW_EMPTY:${PN} = "1"

PACKAGES =+ "${PN}-tools-clippy ${PN}-tools-rustfmt"
FILES:${PN}-tools-clippy = "${bindir}/cargo-clippy ${bindir}/clippy-driver"
FILES:${PN}-tools-rustfmt = "${bindir}/rustfmt"
RDEPENDS:${PN}-tools-clippy = "${PN}"
RDEPENDS:${PN}-tools-rustfmt = "${PN}"

SUMMARY:${PN}-tools-clippy = "A collection of lints to catch common mistakes and improve your Rust code"
SUMMARY:${PN}-tools-rustfmt = "A tool for formatting Rust code according to style guidelines"

rust_do_install() {
    rust_runx install
}

rust_do_install:class-nativesdk() {
    (PSEUDO_UNLOAD=1 rust_runx install)

    install -d ${D}${bindir}
    for i in cargo-clippy clippy-driver rustfmt; do
        cp build/${RUST_BUILD_SYS}/stage2-tools-bin/$i ${D}${bindir}
        chrpath -r "\$ORIGIN/../lib" ${D}${bindir}/$i
    done

    chown root:root ${D}/ -R
    rm ${D}${libdir}/rustlib/uninstall.sh
}

rust_do_install:class-target() {
    (PSEUDO_UNLOAD=1 rust_runx install)

    install -d ${D}${bindir}
    for i in cargo-clippy clippy-driver rustfmt; do
        cp build/${RUST_BUILD_SYS}/stage2-tools-bin/$i ${D}${bindir}
        chrpath -r "\$ORIGIN/../lib" ${D}${bindir}/$i
    done

    chown root:root ${D}/ -R
    rm ${D}${libdir}/rustlib/uninstall.sh
}

RUSTLIB_DEP:class-nativesdk = ""