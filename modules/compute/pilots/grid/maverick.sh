#<ip>:<port> of ComputeServer should be filled for usage
#maverick files should be placed in $HOME/maverick/ directory

export MODULEPATH=${VO_BBMRI_NL_SW_DIR}/modules/:${MODULEPATH}
export WORKDIR=$TMPDIR
source dataTransferSRM.sh

check_process(){
        # check the args
        if [ "$1" = "" ];
        then
                return 0
        fi

        #PROCESS_NUM => get the process number regarding the given thread name
        PROCESS_NUM=$(ps aux | grep "$1" | grep -v "grep" | wc -l)
        if [ $PROCESS_NUM -eq 1 ];
        then
                return 1
        else
                return 0
        fi
}

curl  -F status=started -F backend=ui.grid.sara.nl http://<ip>:<port>/compute/api/pilot > script.sh
bash -l script.sh 2>&1 | tee -a log.log &
#now, done is moved to the actual job
#curl -F status=done -F log_file=@log.log http://<ip>:<port>/compute/api/pilot

#to give some time to start the process
sleep 10

# check wheter the instance of thread exsits
while [ 1 ] ; do
        echo 'begin checking...'
        check_process "script.sh" # the thread name
        CHECK_RET=$?
        if [ $CHECK_RET -eq 0 ]; # none exist
        then
                echo 'NOT RUNNING'
                #time to make sure that job reported back to db
                sleep 20
                cp log.log inter.log
                curl -F status=nopulse -F log_file=@inter.log http://<ip>:<port>/compute/api/pilot
                exit 0
        elif [ $CHECK_RET -eq 1 ];
        then
                echo 'RUNNING'
                cp log.log inter.log
                curl -F status=pulse -F log_file=@inter.log http://<ip>:<port>/compute/api/pilot
        fi
        #this sleep can be modified depending on how often, you like to receive the job status
        #it also depends on the number of running jobs, more jobs -> bigger interval
        sleep 20
done
