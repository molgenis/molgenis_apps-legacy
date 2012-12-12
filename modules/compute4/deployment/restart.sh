kill -9 `lsof -i :8080 -t`
cd molgenis_apps; 
nohup ant -f build_compute.xml runOn -Dport=8080 & 