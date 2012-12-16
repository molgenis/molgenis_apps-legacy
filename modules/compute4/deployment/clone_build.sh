git clone https://github.com/molgenis/molgenis.git
git clone https://github.com/molgenis/molgenis_apps.git
cd molgenis_apps
ant -f build_compute.xml clean-generate-compile
sed -i 's/validate/update/g' build/classes/META-INF/persistence.xml