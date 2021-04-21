.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

*******************
Reproducible Builds
*******************

================
How we define it
================

The Yocto Project defines reproducibility as being where a given input build configuration will give the same binary output regardless of when it is built (now or in 5 years time), regardless of the path on the filesystem the build is run in and regardless of the distro and tools on the underlying host system the build is running on.

==============
Why it matters
==============

The project aligns with the Reproducibile Builds project (https://reproducible-builds.org/) and they have information about why it matters. In addition to being able to validate for security issues being introduced which they talk about at length, from a Yocto Project perspective, it is hugely important that our builds are deterministic. We expect that when you build a given input set of metadata, you get consistent output. This has always been a key focus but is now true down to the binary level including timestamps.

For example, if you find at some point in the future life of a product that you need to rebuild to add a security fix, only the components that have changed should change at the binary level leading to much easier and clearer bounds on where validation is needed.

This also gives an additional benefit to the project builds themselves, our hash equivalence for sstate object reuse works much more effecitvely when the binary outputis unchanging.

===================
How we implement it
===================

We add mappings to the compiler options to ensure debug filepaths are mapped to consistent target compatible paths

We are explict about recipe dependencies and their configuration (no floating configure options or host dependencies creeping in)

We have recipe specific sysroots to isolate recipes so they only see their dependencies.

We filter the tools availble from the host's PATH through HOSTTOOLS.

=========================================
Can we prove the project is reproducible?
=========================================

Yes, we can prove it and we now regularlly test this on the autobuilder. At the time of writing, OE-Core is 100% reproducible for all it's recipes (i.e. world builds) apart from go-lang and ruby's docs package. go-lang has some fundamental problems with reproducibility as it currently always depends upon the paths it is build in unfortunately.

[Info about what we run]

[Add info about diffoscope]

You can see the current status at: https://www.yoctoproject.org/reproducible-build-results/

====================
Can I test my layer?
====================

[Yes, add instructions]





