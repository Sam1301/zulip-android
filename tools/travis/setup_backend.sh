#!/bin/bash
set -e
set -x
tools/provision.py --travis
sudo mkdir -p /var/lib/nagios_state
sudo chown travis /var/lib/nagios_state