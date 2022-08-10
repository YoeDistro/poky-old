#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import os
import stat
import sys
import shutil
import json

import bb.utils
import bb.process

from bblayers.common import LayerPlugin

logger = logging.getLogger('bitbake-layers')

sys.path.insert(0, os.path.dirname(os.path.dirname(__file__)))

import oe.buildcfg

def plugin_init(plugins):
    return MakeSetupPlugin()

class MakeSetupPlugin(LayerPlugin):

    def _write_python(self, input, output):
        with open(input) as f:
            script = f.read()
        with open(output, 'w') as f:
            f.write(script)
        st = os.stat(output)
        os.chmod(output, st.st_mode | stat.S_IEXEC | stat.S_IXGRP | stat.S_IXOTH)

    def _write_json(self, repos, output):
        with open(output, 'w') as f:
            json.dump(repos, f, sort_keys=True, indent=4)

    def _get_repo_path(self, layer_path):
        repo_path, _ = bb.process.run('git rev-parse --show-toplevel', cwd=layer_path)
        return repo_path.strip()

    def _get_remotes(self, repo_path):
        remotes = {}
        remotes_list,_ = bb.process.run('git remote', cwd=repo_path)
        for r in remotes_list.split():
            uri,_ = bb.process.run('git remote get-url {r}'.format(r=r), cwd=repo_path)
            remotes[r] = {'uri':uri.strip()}
        return remotes

    def _get_describe(self, repo_path):
        try:
            describe,_ = bb.process.run('git describe --tags', cwd=repo_path)
        except bb.process.ExecutionError:
            return ""
        return describe.strip()

    def _get_confs(self, conf_path):
        try:
           files = os.listdir(conf_path)
        except:
           return []
        return {f.replace(".conf",""):{} for f in files if f.endswith(".conf")}

    def _get_distros(self, layer_path):
        return self._get_confs(os.path.join(layer_path, "conf/distro"))

    def _get_machines(self, layer_path):
        return self._get_confs(os.path.join(layer_path, "conf/machine"))

    def _get_buildconfigs(self, layerpath):
        return {os.path.relpath(dir, start=layerpath):{} for (dir, subdirs, files) in os.walk(layerpath) if 'local.conf.sample' in files and 'bblayers.conf.sample' in files}

    def _make_repo_config(self, destdir):
        repos = {}
        layers = oe.buildcfg.get_layer_revisions(self.tinfoil.config_data)
        try:
            destdir_repo = self._get_repo_path(destdir)
        except bb.process.ExecutionError:
            destdir_repo = None

        for (l_path, l_name, l_branch, l_rev, l_ismodified) in layers:
            if l_name == 'workspace':
                continue
            if l_ismodified:
                logger.error("Layer {name} in {path} has uncommitted modifications or is not in a git repository.".format(name=l_name,path=l_path))
                return
            repo_path = self._get_repo_path(l_path)
            if repo_path not in repos.keys():
                repos[repo_path] = {'path':os.path.basename(repo_path),'layers':{},'git-remote':{'rev':l_rev, 'branch':l_branch, 'remotes':self._get_remotes(repo_path), 'describe':self._get_describe(repo_path)}}
                if repo_path == destdir_repo:
                    repos[repo_path]['contains_this_file'] = True
                if not repos[repo_path]['git-remote']['remotes'] and not repos[repo_path]['contains_this_file']:
                    logger.error("Layer repository in {path} does not have any remotes configured. Please add at least one with 'git remote add'.".format(path=repo_path))
                    return
            repos[repo_path]['layers'][l_name] = {'subpath':l_path.replace(repo_path,'')[1:]}
            distros = self._get_distros(l_path)
            machines = self._get_machines(l_path)
            buildconfigs = self._get_buildconfigs(l_path)
            if distros:
                repos[repo_path]['layers'][l_name]['distros'] = distros
            if machines:
                repos[repo_path]['layers'][l_name]['machines'] = machines
            if buildconfigs:
                repos[repo_path]['layers'][l_name]['buildconfigs'] = buildconfigs

        top_path = os.path.commonpath([os.path.dirname(r) for r in repos.keys()])

        repos_nopaths = {}
        for r in repos.keys():
            r_nopath = os.path.basename(r)
            repos_nopaths[r_nopath] = repos[r]
            r_relpath = os.path.relpath(r, top_path)
            repos_nopaths[r_nopath]['path'] = r_relpath
        return repos_nopaths

    def do_make_setup(self, args):
        """ Writes out a python script and a json config that replicate the directory structure and revisions of the layers in a current build. """
        repos = self._make_repo_config(args.destdir)
        json = {"version":"1.0","sources":repos}
        if not repos:
            raise Exception("Could not determine layer sources")
        output = args.output_prefix or "setup-layers"
        output = os.path.join(os.path.abspath(args.destdir),output)
        self._write_json(json, output + ".json")
        logger.info('Created {}.json'.format(output))
        if not args.json_only:
            self._write_python(os.path.join(os.path.dirname(__file__),'../../../scripts/oe-setup-layers'), output)
        logger.info('Created {}'.format(output))

    def register_commands(self, sp):
        parser_setup_layers = self.add_command(sp, 'create-layers-setup', self.do_make_setup, parserecipes=False)
        parser_setup_layers.add_argument('destdir',
            help='Directory where to write the output\n(if it is inside one of the layers, the layer becomes a bootstrap repository and thus will be excluded from fetching by the script).')
        parser_setup_layers.add_argument('--output-prefix', '-o',
            help='File name prefix for the output files, if the default (setup-layers) is undesirable.')
        parser_setup_layers.add_argument('--json-only', action='store_true',
            help='Write only the layer configuruation in json format. Otherwise, also a copy of poky/scripts/oe-setup-layers is provided, which is a self contained python script that fetches all the needed layers and sets them to correct revisions using the data from the json.')
