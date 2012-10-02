#!/bin/bash                                                                                                             
#PBS -N maverick                                                                                                        
#PBS -q gcc                                                                                                             
#PBS -l nodes=1:ppn=4                                                                                                   
#PBS -l walltime=00:30:00                                                                                               
#PBS -l mem=4GB                                                                                                         
#PBS -e maverick.err                                                                                                    
#PBS -o maverick.out                                                                                                    
#PBS -W umask=0007                                                                                                      

export WORKDIR=/target/gpfs2/gcc/home/mdijkstra/computeScripts

source /target/gpfs2/gcc/tools/scripts/dataTransfer.sh

curl  -F status=started http://129.125.141.171:8080/compute/api/pilot > $WORKDIR/script$1.sh
sh $WORKDIR/script$1.sh 2>&1 | tee -a $WORKDIR/script$1.log
curl -F status=done -F log_file=@$WORKDIR/script$1.log http://129.125.141.171:8080/compute/api/pilot
