#!/bin/bash -e

echo -n 'Copy system startup script...'
sudo cp etc/rc.local /etc/rc.local
echo '[OK]'

#echo -n 'Copy database with empty scheme...'
#cp tcontrol-db.mv.db ~/tcontrol-db.mv.db
#echo  '[OK]'

echo -n 'Copy 3g modem startup soft...'
cp -r 3g ~/
echo '[OK]'

echo -n 'Unpac tomee...'
mkdir apache-tomee-plus-1.7.1
cd apache-tomee-plus-1.7.1
tar -xzf ../apache-tomee-plus-1.7.1.tgz
echo '[OK]'

echo -n 'Move tomee to installation folder...'
cd ..
mv  apache-tomee-plus-1.7.1 ~/
echo '[OK]'

#echo -n 'Install H2 database driver...'
#cp h2-1.4.190.jar ~/apache-tomee-plus-1.7.1/lib/
#echo '[OK]'

echo -n 'Copy .profile to set system variables as JAVA_HOME..'
cp  etc/.profile ~/
echo '[OK]'

echo -n 'Unpack java machime for RaspberryPI...'
tar -xzf jdk-8u65-arm.tar.gz
echo '[OK]'
echo -n 'Move unpacked java to installation folder...'
mv jdk1.8.0_65 ~/
echo '[OK]'
 
echo -n 'Install pi config...' 
sudo cp etc/config.txt /boot/
echo '[OK]'

echo -n 'Install system updates...'
sudo dpkg -i apt/archives/*.deb
echo '[OK]'

echo -n 'Load kernel module w1-gpio...'
sudo modprobe w1-gpio
echo '[OK]'

echo -n 'Load kernel module w1-therm...'
sudo modprobe w1-therm
echo '[OK]'

