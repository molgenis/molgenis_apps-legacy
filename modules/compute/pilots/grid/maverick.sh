export WORKDIR=$TMPDIR
source dataTransferSRM.sh
curl  -F status=started http://<ip>:<port>/compute/api/pilot > script.sh
sh script.sh 2>&1 | tee -a log.log
curl -F status=done -F log_file=@log.log http://<ip>:<port>/compute/api/pilot