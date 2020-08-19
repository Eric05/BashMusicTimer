# BashMusicTimer
The bash Version of Music Timer

___Start Command in Ubuntu__________________________________________________________

gnome-terminal -e "bash -i -c '/home/user//Shutdown_3.0/Main.sh -l;bash'"
 optional: bash -c 'termX;$SHELL'

___Workaround to use relative path /from stackoverflow.com/questions/43823575____________

scriptdir="$(dirname "$0")"
cd "$scriptdir"
cwd=$(pwd)


___If Screen doesnt stay black_______________________________________________________ 

 sleep 9 && wmctrl -k on; changes to desktop


___Bluetooth_________________________________________________________________________

Get Mac Adress with bt-device -l after installing bluez-tools / Ubuntu


___Third Party Apps___________________________________________________________________

All availabe under packet manager ( ubuntu and debian tested)
 - sed
 - bluez-tools
 - xdotool
 - xset dpms
 - wmctrl 
 - firefox


___Program Flow_______________________________________________________________________

nohup:
    Üblicherweise nutzt man nohup, um Dienste im Hintergrund zu starten und diese so von der Login-Shell zu trennen. 
    Ausgaben des aufgerufenen Programms werden automatisch in die Datei nohup.out geleitet, die im Verzeichnis angelegt wird, von dem aus der Befehl ausgeführt wurde. 
    Um zu verhindern, dass das Programm nicht weiterläuft, weil es auf Eingaben wartet, ist es außerdem manchmal notwendig, auch die Standard-Eingabe etwa mit >/dev/null umzulenken.

___Start Internet Radio_____________________________________________________________

var play = document.querySelectorAll('.player__button'); // '.player__button--stopped');

for (let i = 0; i < play.length; ++i) {
  play[i].click();
};

