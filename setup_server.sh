#!/bin/bash
git clone https://github.com/zulip/zulip.git
./tools/provision.py
source /srv/zulip-venv/bin/activate
./tools/run-dev.py
