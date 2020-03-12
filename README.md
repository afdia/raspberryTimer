Im Folgenden sind die Setup Schritte für den Raspberry beschrieben

# raspberryTimer
Raspberry Timer mit Alarm. Autostart wenn Raspberry angehängt

# Raspberry Debian (Raspbian) installieren
* als pw hab ich "asdf" genommen

Nach Installation:
1. ssh aktivieren: http://raspberrypiguide.de/howtos/ssh-zugriff-unter-raspbian-einrichten/
2. audo auf 3.5mm fixieren: https://stackoverflow.com/questions/30377129/playing-audio-on-raspberry-pi-with-java/30520683#30520683
3. install openjdk: https://www.raspberrypi.org/forums/viewtopic.php?t=199813
4. sound für openjdk aktivieren: https://nealvs.wordpress.com/2017/08/11/java-sound-on-a-raspberry-pi-with-openjdk/
5. mit scp jar file kopieren: http://www.hypexr.org/linux_scp_help.php
zb: <git_repo>/raspberrypi/target > scp raspberrypi-0.0.1-SNAPSHOT.jar pi@192.168.0.12:/home/pi

# Command um Raspberry zu rebooten
`sudo reboot`

# LCD SETUP (https://www.youtube.com/watch?v=Fj3wq98pd20)
* `sudo rm -rf LCD-show`
* `git clone https://github.com/goodtft/LCD-show.git`
* `chmod -R 755 LCD-show`
* `cd LCD-show/`
* `sudo ./LCD35-show`
* nach neustart funktioniert display

## Java GUI
* für java gui programm benötigt man folgenden aufruf (https://forum-raspberrypi.de/forum/thread/34526-jar-datein-oeffnen/):
* `export DISPLAY=:0`
* um das setting permanent zu machen (https://stackoverflow.com/questions/13046624/how-to-permanently-export-a-variable-in-linux):
* `sudo nano /etc/environment`
* folgende Zeile eintragen: `DISPLAY=:0 m` (evtl ohne m am Ende?)

### Java GUI autostart einrichten: https://www.youtube.com/watch?v=VQkvh6d41Y0
1. lege shellscript wrapper für Java Aufruf an
* `sudo nano ~/timer.sh -> Inhalt "java -jar raspberry.jar"`
* `sudo chmod ugo+x ~/timer.sh`

2. shellscript beim Start des Terminals starten
* `sudo nano ~/.bashrc`
ganz unten als neue letzte Zeile "bash timer.sh" hinzufügen

3. Terminal bei Systemstart starten
* `sudo nano /etc/xdg/lxsession/LXDE-pi/autostart`
unten folgende 3 Zeilen hinzufügen
```
@xset s off
@xset -dpms
@lxterminal
```
(die ersten 2 Zeilen sorgen dafür dass der Bildschirm nicht mehr schwarz wird (https://www.raspberrypi.org/forums/viewtopic.php?t=211855), die letzte Zeile startet das Terminal beim Systemstart)

4. Raspberry auf Desktop booten lassen via "sudo raspi-config" (ist default bei neuem raspian)
