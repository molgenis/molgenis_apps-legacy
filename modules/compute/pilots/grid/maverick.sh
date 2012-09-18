export WORKDIR=$TMPDIR
source dataTransferSRM.sh
curl  -F status=started http://129.125.141.171:8080/compute/api/pilot > script.sh
sh script.sh 2>&1 | tee -a log.log
curl -F status=done -F log_file=@log.log http://129.125.141.171:8080/compute/api/pilot