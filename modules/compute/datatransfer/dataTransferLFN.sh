#!/bin/bash

getRemoteLocation()
{
    ARGS=($@)
    myFile=${ARGS[0]}
    remoteFile=lfn://grid/bbmri.nl/byelas${myFile:`expr length $TMPDIR`}
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
	
	# 2. cp lfn:.../remoteFile myFile
	echo "lcg-cp $remoteFile file:///$myFile"
	lcg-cp $remoteFile file:///$myFile
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
	
	echo "lcg-cr -l $remoteFile file:///$myFile"
	lcg-cr -d srm.grid.sara.nl -l $remoteFile file:///$myFile
	else
	echo "Example usage: getData \"\$TMPDIR/datadir/myfile.txt\""
	fi
}

export -f getRemoteLocation
export -f getFile
export -f putFile
