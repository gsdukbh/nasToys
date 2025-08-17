#!/bin/bash

# This script installs the necessary dependencies for the project.
# install jdk
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk version
sdk install java 21.0.2-graalce

# copy
cp nastoys.service /etc/systemd/system/nastoys.service
#
systemctl enable nastoys.service
systemctl start nastoys.service
systemctl status nastoys.service