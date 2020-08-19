#!/bin/bash

# workaround to use relative path-------------------------------------------------------#

scriptdir="$(dirname "$0")"
cd "$scriptdir"
cwd=$(pwd)

#----visual effect ------------------------------------------------------------------------#

function VisualEffect {

for i in {28..47} {47..28} ; do echo -en \ "\e[48;5;${i}m \e[0m" ; done ; echo 
}

#--- test if akku is charged --------------------------------------------------------------#

function TestAkku {

akkuWarning=$(sed -n 7p Settings)
state=$(upower -i $(upower -e | grep 'BAT') | grep -E "percentage" | grep -o -E '[0-9]+')
discharge=$(upower -i $(upower -e | grep 'BAT') | grep -E "state" | grep -o -E 'discharging')

echo -e "\n Battery :\t" $state "% | " $discharge
if [[ $(($state + 1)) -lt $akkuWarning && $discharge == "discharging" ]]; then
    echo -e "\n\e[31mPlease charge Battery and Press Enter when done\e[0m"

while true; do
     sleep 3 && aplay /usr/share/sounds/linuxmint-gdm.wav &> /dev/null;
done &

read
kill $!
wait $! 2>/dev/null
fi
}

#----change settings ---------------------------------------------------------------#

function ChangeSettings {

clear
VisualEffect
echo -e "\e[1m\e[96m"""

# sed -n 'p;n' Playlists -> read list line by line and increase counter
i=0; while read -r line; 
do [ $((i++)) ] && echo -e $i "\t" "$line" | cut -d';' -f1; 
done < $cwd/Playlist
echo -e "\e[0m"""
read -p "Choose Playlist: " playNumber

if [[ $playNumber -ne "" ]]; then
    playLine=$(sed -n "$playNumber"p Playlist)
    # need to use # as delimeter because of the slashes in path 
    sed -i "1s#.*#$playLine#" $cwd/Settings
fi

read -p "Choose Sleeptime: " shutTime

if [[ $shutTime -ne "" ]]; then
    sed -i 2s"/.*/$shutTime/" $cwd/Settings
fi

echo ""
VisualEffect

}

#----start playlist and connect bluetooth ------------------------------------------#

function StartPlay {

playlist=$(sed -n 1p Settings| cut -d';' -f1)
playlistPath=$(sed -n 1p Settings| cut -d';' -f2)
macName=$(sed -n 3p Settings| cut -d';' -f1)
macAddress=$(sed -n 3p Settings| cut -d';' -f2)
blackScreen=$(sed -n 8p Settings)
pushTerminal=$((blackScreen-2)) 

bluetoothctl <<<"connect $macAddress" &> /dev/null &
eval $playlistPath &> /dev/null &
sleep $pushTerminal && xdotool windowminimize $(xdotool getactivewindow)

}

#---warning shutdown ---------------------------------------------------------------#

function Warning {

xset dpms force on 
wmctrl -a terminal

clear
sleep 2 && aplay /usr/share/sounds/linuxmint-gdm.wav &> /dev/null 
sleep 2 && aplay /usr/share/sounds/linuxmint-gdm.wav &> /dev/null 

echo -e "\n\e[31mShutdown in 2min \e[0m" 
return



}

#----start shutdown, start warning and make screen black -------------------------------#

function StartShut {

tempShut=$(sed -n 6p Settings)
blackScreen=$(sed -n 8p Settings)
shutWarning=$(sed -n 5p Settings)
warn=$(($1-$shutWarning))

pkill sleep
shutdown -c

shutdown -h $1
(sleep ${warn}m && Warning) &
sleep $blackScreen && xset dpms force off &

echo -e "\e[1m\e[96m"""
sleep 1s && echo "  * Press Enter to increase timer for "$tempShut "minutes."
echo "  * Enter 'f' to cancel and start Firefox."
echo "  * Enter 'c' to change program."
echo "  * Enter any time to increase timer."
echo "  * Enter any key to cancel."
read -p "" newShut

if [[ "$newShut" == "" ]]; then
   
    StartShut $tempShut    
fi

if [[ "$newShut" == "C" || "$newShut" == "c" ]]; then
    pkill vlc
    pkill firefox
    ChangeSettings &&
    shutTime=$(sed -n 2p Settings)
    StartPlay &
    StartShut $shutTime  
fi

if [[ "$newShut" == "F" || "$newShut" == "f" ]]; then
    # check if firefox is running, else start 
    lineCount="$(ps aux | grep firefox | wc -l)"
	if [ "$lineCount" -gt 1 ]; then
        wmctrl -R firefox;
	else
        firefox &> /dev/null &
  	fi
    shutdown -c
    pkill terminal; 
fi

if ! [ "$newShut" -eq "$newShut" ] 2> /dev/null; then
    shutdown -c
    pkill terminal
fi

if [[ "$newShut" -lt 10 && "$newShut" -gt 0 ]]; then
    StartShut 10 
fi

if [[ "$newShut" == "$newShut" ]]; then   
    StartShut $newShut 
fi

}

#---main --------------------------------------------------------------------- #

playLine=$(sed -n 1p Settings)
playlist=$(sed -n 1p Settings| cut -d';' -f1)
playlistPath=$(sed -n 1p Settings| cut -d';' -f2)
shutTime=$(sed -n 2p Settings)
macName=$(sed -n 3p Settings| cut -d';' -f1)
macAddress=$(sed -n 3p Settings| cut -d';' -f2)
inputLength=$(sed -n 4p Settings)

clear
VisualEffect
TestAkku 

echo -e " \e[1m\e[96mPlaying:\t" $playlist
echo -e " Sleep in:\t" $shutTime "min"
echo -e " Connecting:\t "$macName
echo ""
echo -e " \e[0mPress any key to change settings"
read -t$inputLength input 

if [[ -n "$input"  || "$input" -ne "" ]]; then
    ChangeSettings 
fi

shutTime=$(sed -n 2p Settings)

StartPlay &
StartShut $shutTime 















