#!/bin/bash
 # Student:    Trung Nguyen, Yat Shing Pang
 # Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 # Course:     CSE 4232
 # Project:    GOSSIP P2P, Milestone 4
#
path=$(pwd)


command="$path/script/run.sh -T "$*

#echo $command

$command
