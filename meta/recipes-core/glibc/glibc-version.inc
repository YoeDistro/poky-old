SRCBRANCH ?= "master"
PV = "2.36"
SRCREV_glibc ?= "7c5db7931f940a0de9d39b566f6fef41148491c0"
SRCREV_localedef ?= "794da69788cbf9bf57b59a852f9f11307663fa87"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
