#!/bin/bash
#
 # Student:    Trung Nguyen, Yat Shing Pang
 # Email:      tnguyen2013@my.fit.edu, apang2013@my.fit.edu
 # Course:     CSE 4232
 # Project:    GOSSIP P2P, Milestone 4
#
path=$(pwd)
library="$path/library/commons-cli-1.3.1.jar"
cd source
javac -d "$path/bin" ASNObj.java
javac -d "$path/bin" ASNObjArrayable.java
javac -d "$path/bin" ASN1_Util.java
javac -d "$path/bin" ASN1DecoderFail.java
javac -d "$path/bin" ASNLenRuntimeException.java
javac -d "$path/bin" Decoder.java
javac -d "$path/bin" Encoder.java
javac -d "$path/bin" -cp .:$library Client.java
javac -d "$path/bin" -cp .:$library GET_OPT.java
javac -d "$path/bin" -cp .:$library GET_OPT_CLIENT.java
javac -d "$path/bin" -cp .:$library GET_OPT_SERVER.java
javac -d "$path/bin" -cp .:$library GET_OPT_UI.java
javac -d "$path/bin" Gossip.java
javac -d "$path/bin" hello_client.java
javac -d "$path/bin" -cp .:$library NPServer.java
javac -d "$path/bin" Peer.java
javac -d "$path/bin" PeersAnswer.java
javac -d "$path/bin" PeersQuery.java
javac -d "$path/bin" P_Input.java
javac -d "$path/bin" TCPServer.java
javac -d "$path/bin" TCPThread.java
javac -d "$path/bin" UDPBroadcast.java
javac -d "$path/bin" UDPclient.java
javac -d "$path/bin" UDPServer.java
javac -d "$path/bin" -cp .:$library NPServer.java
javac -d "$path/bin" -cp .:$library UI.java
