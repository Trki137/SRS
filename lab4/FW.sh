#! /bin/sh
#
# Dodajte ili modificirajte pravila na oznacenim mjestima ili po potrebi (i želji) na 
# nekom drugom odgovarajucem mjestu (pazite: pravila se obrađuju slijedno!)
#
IPT=/sbin/iptables

$IPT -P INPUT DROP
$IPT -P OUTPUT DROP
$IPT -P FORWARD DROP

$IPT -F INPUT
$IPT -F OUTPUT
$IPT -F FORWARD

$IPT -A INPUT   -m state --state ESTABLISHED,RELATED -j ACCEPT
$IPT -A OUTPUT  -m state --state ESTABLISHED,RELATED -j ACCEPT
$IPT -A FORWARD -m state --state ESTABLISHED,RELATED -j ACCEPT

#
# za potrebe testiranja dozvoljen je ICMP (ping i sve ostalo)
#
$IPT -A INPUT   -p icmp -j ACCEPT
$IPT -A FORWARD -p icmp -j ACCEPT
$IPT -A OUTPUT  -p icmp -j ACCEPT

#
# Primjer "anti spoofing" pravila na sucelju eth0
#
#$IPT -A INPUT   -i eth0 -s 127.0.0.0/8  -j DROP
#$IPT -A FORWARD -i eth0 -s 127.0.0.0/8  -j DROP
#$IPT -A INPUT   -i eth0 -s 198.51.100.0/24  -j DROP
#$IPT -A FORWARD -i eth0 -s 198.51.100.0/24  -j DROP
#$IPT -A INPUT   -i eth0 -s 10.0.0.0/24  -j DROP
#$IPT -A FORWARD -i eth0 -s 10.0.0.0/24  -j DROP

#
# Web poslužitelju (tcp /80) i DNS poslužitelju (udp/53 i tcp/53) pokrenutima na www se može 
# pristupiti s bilo koje adrese (iz Interneta i iz lokalne mreže), ...
#
# <--- Dodajte pravila (ako je potrebno)

$IPT -A FORWARD -p tcp -d 198.51.100.10 --dport 80 -j ACCEPT
$IPT -A FORWARD -p udp -d 198.51.100.10 --dport 53 -j ACCEPT
$IPT -A FORWARD -p tcp -d 198.51.100.10 --dport 53 -j ACCEPT

#
# ... a SSH poslužitelju (na www) samo s racunala PC iz lokalne mreže (LAN)
# 
# <--- Dodajte pravila (ako je potrebno)
$IPT -A FORWARD -s 10.0.0.20/24 -d 198.51.100.10 -p tcp --dport 22 -j ACCEPT
# 
# S www je dozvoljen pristup poslužitelju database (LAN) na TCP portu 10000 te pristup 
# DNS poslužiteljima u Internetu (UDP i TCP port 53).
#
# <--- Dodajte pravila (ako je potrebno)
$IPT -A FORWARD -s 198.51.100.10 -d 203.0.113.1/24 -p tcp --dport 53 -j ACCEPT
$IPT -A FORWARD -s 198.51.100.10 -d 203.0.113.1/24 -p udp --dport 53 -j ACCEPT
#
# ... S www je zabranjen pristup svim ostalim adresama i poslužiteljima.
#
# <--- Dodajte pravila (ako je potrebno)
##Ne treba nista dodavati
#
#
# Pristup svim ostalim adresama i poslužiteljima u DMZ je zabranjen.
#
# <--- Dodajte pravila (ako je potrebno)
## Ne treba nista dodavati 1.4
#
# Pristup SSH poslužitelju na cvoru database, koji se nalazi u lokalnoj mreži LAN, 
# dozvoljen je samo racunalima iz mreže LAN.
#
# <--- Dodajte pravila (ako je potrebno)
#Ne treba nista dodati 2.1
#
# Web poslužitelju na cvoru database, koji sluša na TCP portu 10000, može se pristupiti
# iskljucivo s racunala www koje se nalazi u DMZ (i s racunala iz mreže LAN).
#
# <--- Dodajte pravila (ako je potrebno)
##Ne treba nista dodavati
#
# S racunala database je zabranjen pristup svim uslugama u Internetu i u DMZ.
#
# <--- Na odgovarajuce mjesto dodajte pravila (ako je potrebno)
$IPT -I FORWARD 3 -s 10.0.0.100 -d 203.0.113.1/24 -j DROP
$IPT -I FORWARD 3 -s 10.0.0.100 -d 198.51.100.1/24 -j DROP
$IPT -I FORWARD 3 -s 198.51.100.10 -d 10.0.0.100 -p tcp --dport 10000 -j ACCEPT
# Zabranjen je pristup svim ostalim uslugama na poslužitelju database (iz Interneta i iz DMZ)
#
# <--- Na odgovarajuce mjesto dodajte pravila (ako je potrebno)
##Ne treba nista dodati
#
# S racunala iz lokalne mreže (osim s database) se može pristupati svim racunalima u Internetu 
# ali samo korištenjem protokola HTTP (tcp/80) i DNS (udp/53 i tcp/53).
#
# <--- Dodajte pravila (ako je potrebno)
$IPT -A FORWARD -s 10.0.0.20 -d 203.0.113.1/24 -p tcp --dport 80 -j ACCEPT
$IPT -A FORWARD -s 10.0.0.20 -d 203.0.113.1/24 -p tcp --dport 53 -j ACCEPT
$IPT -A FORWARD -s 10.0.0.20 -d 203.0.113.1/24 -p udp --dport 53 -j ACCEPT
#
# Pristup iz vanjske mreže u lokalnu LAN mrežu je zabranjen.
#
# <--- Dodajte pravila (ako je potrebno)
##Ne treba nista dodati
#
# Na FW je pokrenut SSH poslužitelj kojem se može pristupiti samo iz lokalne mreže i to samo sa cvora PC.
#
# <--- Dodajte pravila (ako je potrebno)
$IPT -A INPUT -s 10.0.0.20 -p tcp --dport 22 -j ACCEPT
#
# Pristup svim ostalim uslugama (portovima) na cvoru FW je zabranjen.
#
# <--- Dodajte pravila (ako je potrebno)

#Ovo je pravilo za Internet

$IPT -I FORWARD 7 -d 203.0.113.10 -p tcp --dport 22 -j ACCEPT

