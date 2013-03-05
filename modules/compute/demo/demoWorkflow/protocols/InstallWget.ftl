#FOREACH project

#
##
### Exit if wget exists already (exit code 0)
##
#
if [[ $(which wgetx) ]]; then exit 0; fi;

#
##
### Else install wget on your Mac
##
#

mkdir -p ~/tmp/wget/
cd ~/tmp/wget/

#First, use curl to download the latest wget source:
curl -O http://ftp.gnu.org/gnu/wget/wget-1.13.4.tar.gz

#Next we use tar to uncompress the files you just downloaded:
tar -xzf wget-1.13.4.tar.gz

#Use cd to change to the directory:
cd wget-1.13.4

#Configure with the appropriate –with-ssl flag to prevent a “GNUTLS not available” error:
./configure --with-ssl=openssl

#Build the source:
make

#Install wget, it ends up in /usr/local/bin/:
sudo make install

#Confirm everything worked by running wget:
wget --help

#Clean up by removing wget source files when finished:
cd .. && rm -rf wget*

#You’re all set, enjoy wget in Mac OS X.