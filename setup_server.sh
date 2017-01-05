#!/bin/bash
git clone https://github.com/zulip/zulip.git
cd zulip
./tools/provision.py
source /srv/zulip-venv/bin/activate
./tools/run-dev.py
