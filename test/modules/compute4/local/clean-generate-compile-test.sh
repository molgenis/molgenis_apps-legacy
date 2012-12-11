#git clone https://github.com/molgenis/molgenis.git
#git clone https://github.com/molgenis/molgenis_apps.git

ant -f ../../../../build_compute.xml clean-generate-compile
sh test.sh