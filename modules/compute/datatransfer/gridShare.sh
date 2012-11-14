#!/bin/bash

GRIDROOT="srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/byelas"
CLUSTERROOT="gbyelas@clustervp:/target/gpfs2/gcc/home/gbyelas"

getRemoteLocation()
{
    ARGS=($@)
    myFile=${ARGS[0]}
    remoteFile=$GRIDROOT${myFile:`expr length $WORKDIR`}
    echo $remoteFile
}

getRemoteLocationSimple()
{
    ARGS=($@)
    myFile=${ARGS[0]}
    var=${#GRIDROOT}
    remoteFile=${myFile:$var}
    echo $remoteFile
}

copyFile()
{
	echo "file transfer from cluster to SRM"
	ARGS=($@)
    remoteFile=${ARGS[0]}		
	name=${remoteFile##*/}
    xpath=${remoteFile%/*}
    echo "name = $name"
    echo "path = $xpath"
    simplePath=`getRemoteLocationSimple $xpath`
	echo "Path = $simplePath"
	clusterFile="$simplePath"/"$name"
	echo "clusterFile = $clusterFile"
	
	echo "check if path exist in srm storage"
    echo "srmls -l $xpath"
    srmls -l $xpath
    	
	returnCode=$?
    
    if [ $returnCode -eq "0" ];
    then
    	echo "srm directory $xpath exists"
    elif [ $returnCode -eq "1" ];
    then
		echo "srm directory $xpath does not exist"

		IFS='/' read -a array <<< "$simplePath"
		dir="$GRIDROOT"
					
		for element in "${array[@]}"
		do
    		echo "$element"
    		echo "create remote directory"
    		dir="$dir/$element"
    		echo "srmmkdir $dir"
    		srmmkdir $dir
    		echo " "
		done
	fi
    	
    echo "copy file from cluster to UI as a buffer place"
    echo "scp -i $CLUSTERROOT$clusterFile $HOME/tmptransfer/$name"
    scp $CLUSTERROOT$clusterFile $HOME/tmptransfer/$name
    echo " "
    echo "copy file from UI to srm"
    echo "srmcp -server_mode=passive file:///$HOME/tmptransfer/$name $remoteFile"
    srmcp -server_mode=passive file:///$HOME/tmptransfer/$name $remoteFile
    echo " "
    echo "remove file at UI node"
    echo "rm $HOME/tmptransfer/$name"
    rm $HOME/tmptransfer/$name
    echo " "
	#return 0
}

getFile()
{
    ARGS=($@)
    NUMBER="${#ARGS[@]}";
    if [ "$NUMBER" -eq "2" ]
	then

		echo " "
		myFile=${ARGS[0]}
		flag=${ARGS[1]}
		flagRest="-rest"
		flagForce="-force"
		
		remoteFile=`getRemoteLocation $myFile`

		echo "create directory on execution node"
		echo "mkdir -p $(dirname "$myFile")"
		mkdir -p $(dirname "$myFile")
		echo " "
				
		# 2. check if file exist
		echo "check if file exists in srm storage"
    	echo "srmls -l $remoteFile"
    	srmls -l $remoteFile
    	returnCode=$?
    
    	#if  [[ $returnCode -eq "0" ]] && [[ $flag -eq "-force" ]];
    	if  [ "x$flag" == "x$flagForce" ];
    	then
    		if [ $returnCode -eq "0" ];
    		then
    			echo "$remoteFile will be removed from SRM"
    			echo "srmrm $remoteFile"
    			srmrm $remoteFile
    			echo " "
    			copyFile "$remoteFile"
    		fi	
    	elif [ "x$flag" == "x$flagRest" ];
    	then
    		if [ $returnCode -eq "0" ];
    		then
    			echo "file $remoteFile exists"
    	
    		elif [ $returnCode -eq "1" ];
    		then
    			echo "$remoteFile does not exist in SRM"
				
				copyFile "$remoteFile"
			fi
		fi
    	
    	#put file to execution node
			echo "copy file from srm storage to the execution node"
			echo "srmcp -server_mode=passive $remoteFile file:///$myFile"
			srmcp -server_mode=passive $remoteFile file:///$myFile
			echo " "
			echo "change permission of the transfered file, that is needed for executables"
			echo "chmod 755 $myFile"
			chmod 755 $myFile
    	
	else
		echo "Example usage: getData \"\$WORKDIR/datadir/myfile.txt\" [-rest|-force]"
	fi
}

export -f getFile

