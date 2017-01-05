#!/bin/bash
git clone https://github.com/zulip/zulip.git
sudo apt-get install closure-compiler libfreetype6-dev libffi-dev \
	         memcached rabbitmq-server libldap2-dev redis-server \
		     postgresql-server-dev-all libmemcached-dev python-dev \
		         hunspell-en-us nodejs nodejs-legacy npm git yui-compressor \
			     puppet gettext postgresql
sudo add-apt-repository -ys ppa:groonga/ppa
sudo apt-get update
cd zulip
sudo apt-add-repository -ys ppa:tabbott/zulip
sudo apt-get update
wget https://dl.dropboxusercontent.com/u/283158365/zuliposs/postgresql-9.1-tsearch-extras_0.1.2_amd64.deb
sudo dpkg -i postgresql-9.1-tsearch-extras_0.1.2_amd64.deb
sudo virtualenv /srv/zulip-venv -p python2 # Create a python2 virtualenv
sudo chown -R `whoami`:`whoami` /srv/zulip-venv
source /srv/zulip-venv/bin/activate # Activate python2 virtualenv
pip install --upgrade pip # upgrade pip itself because older versions have known issues
pip install --no-deps -r requirements/py2_dev.txt # install python packages required for development

sudo virtualenv /srv/zulip-py3-venv -p python3 # Create a python3 virtualenv
sudo chown -R `whoami`:`whoami` /srv/zulip-py3-venv
source /srv/zulip-py3-venv/bin/activate # Activate python3 virtualenv
pip install --upgrade pip # upgrade pip itself because older versions have known issues
pip install --no-deps -r requirements/py3_dev.txt # install python packages required for development

./tools/install-mypy
./tools/setup/emoji/build_emoji
./scripts/setup/generate_secrets.py --development
if [ $(uname) = "OpenBSD"  ]; then sudo cp ./puppet/zulip/files/postgresql/zulip_english.stop /var/postgresql/tsearch_data/; else sudo cp ./puppet/zulip/files/postgresql/zulip_english.stop /usr/share/postgresql/9.*/tsearch_data/; fi
./scripts/setup/configure-rabbitmq
./tools/setup/postgres-init-dev-db
./tools/do-destroy-rebuild-database
./tools/setup/postgres-init-test-db
./tools/do-destroy-rebuild-test-database
./manage.py compilemessages
sudo ./tools/setup/install-node
npm install
./tools/run-dev.py
