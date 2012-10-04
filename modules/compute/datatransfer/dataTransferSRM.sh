#!/bin/bash

getRemoteLocation()
{
    ARGS=($@)
    myFile=${ARGS[0]}
    remoteFile=srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas${myFile:`expr length $TMPDIR`}
    echo $remoteFile
}

getFile()
{
    ARGS=($@)
    NUMBER="${#ARGS[@]}";
    if [ "$NUMBER" -eq "1" ]
	then

	myFile=${ARGS[0]}
	remoteFile=`getRemoteLocation $myFile`

	# 1. myPath = getPath( myFile ) will strip off the file name and return the path
	mkdir -p $(dirname "$myFile")
	
	# 2. cp srm:.../remoteFile myFile
	echo "srmcp -server_mode=passive $remoteFile file:///$myFile"
	srmcp -server_mode=passive $remoteFile file:///$myFile
	chmod 755 $myFile

	else
	echo "Example usage: getData \"\$TMPDIR/datadir/myfile.txt\""
	fi
}

putFile()
{
    ARGS=($@)
    NUMBER="${#ARGS[@]}";
    if [ "$NUMBER" -eq "1" ]
	then
	myFile=${ARGS[0]}
	remoteFile=`getRemoteLocation $myFile`
	echo "srmcp -server_mode=passive file:///$myFile $remoteFile"
	srmcp -server_mode=passive file:///$myFile $remoteFile
	else
	echo "Example usage: getData \"\$TMPDIR/datadir/myfile.txt\""
	fi
}

export -f getRemoteLocation
export -f getFile
export -f putFile
