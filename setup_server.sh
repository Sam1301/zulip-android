#!/bin/bash
git clone https://github.com/zulip/zulip.git
cd zulip
./tools/provision.py
./tools/run-dev.py 
