#!/bin/bash
 # Student:    Trung Nguyen, Yat Shing Pang
 # Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 # Course:     CSE 4232
 # Project:    GOSSIP P2P, Milestone 3
#
path=$(pwd)
library="$path/library/commons-cli-1.3.1.jar"

cd bin
command="java -cp .:"$library" NPServer "$@
echo $command
$command
