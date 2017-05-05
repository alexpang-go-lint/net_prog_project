#!/bin/bash
#
 # Student:    Trung Nguyen, Yat Shing Pang
 # Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 # Course:     CSE 4232
 # Project:    GOSSIP P2P, Milestone 5
#

path=$(pwd)
file=($path/data/test.txt)
library="$path/library/commons-cli-1.3.1.jar"
cd bin
java -cp .:"$library" UI -s 127.0.0.1 -p 3333 < $file
