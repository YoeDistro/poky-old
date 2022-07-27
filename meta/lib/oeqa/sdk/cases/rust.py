#
# SPDX-License-Identifier: MIT
#

import os
import shutil
import unittest

from oeqa.core.utils.path import remove_safe
from oeqa.sdk.case import OESDKTestCase

from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class RustCompileTest(OESDKTestCase):
    td_vars = ['MACHINE']

    @classmethod
    def setUpClass(self):
        targetdir = os.path.join(self.tc.sdk_dir, "hello")
        try:
            os.removedirs(targetdir)
        except OSError:
            pass
        shutil.copytree(os.path.join(self.tc.sdk_files_dir, "rust/hello"), targetdir)

    def setUp(self):
        machine = self.td.get("MACHINE")
        if not self.tc.hasHostPackage("packagegroup-rust-cross-canadian-%s" % machine):
            raise unittest.SkipTest("RustCompileTest class: SDK doesn't contain a Rust cross-canadian toolchain")

    def test_cargo_build(self):
        self._run('cd %s/hello; cargo build' % self.tc.sdk_dir)
