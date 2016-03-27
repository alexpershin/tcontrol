#!/bin/sh

echo 'Copy system startup script...'
sudo cp etc/rc.local /etc/rc.local
echo -n '[OK]'

echo 'Copy database with empty scheme...'
cp tcontrol-db.mv.db ~/tcontrol-db.mv.db
echo -n '[OK]'

echo 'Copy 3g modem startup soft...'
cp -r 3g ~/
echo -n '[OK]'

echo 'Unpac tomee...'
tar -xzf apache-tomee-plus-1.7.1.tgz
echo -n '[OK]'

echo 'Move tomee to installation folder...'
mv  apache-tomee-plus-1.7.1 ~/
echo -n '[OK]'

echo 'Copy .profile to set system variables as JAVA_HOME..'
cp  etc/.profile ~/
echo -n '[OK]'

echo 'Unpack java machime for Raspberry IP...'
tar -xzf jdk-8u65-arm.tar.gz
echo -n '[OK]'
echo 'Move unpacked java to installation folder...'
mv jdk1.8.0_65 ~/
echo -n '[OK]'
 
echo 'Install pi config...' 
sudo cp etc/config.txt /boot/
echo -n '[OK]'

echo 'Install system updates...'
sudo dpkg -l apt/archives/*.deb
echo -n '[OK]'

echo 'Load kernel module w1-gpio...'
sudo modprobe w1-gpio
echo -n '[OK]'

echo 'Load kernel module w1-therm...'
sudo modprobe w1-therm
echo -n '[OK]'

