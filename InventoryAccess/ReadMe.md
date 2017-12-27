#Inventory Management Java Application
This version is to be run on the Mac because it telnets over to the raspberry pi to execute it.

###Purpose
This application reaches out to the local Phant server to look at two different streams.
The first stream is supplied with information from the Particle Photon and is cleared once information is read out.
The second stream will be used in the future and will be used to allow the photon to dynamically create inventory item
lists with the current quantity.
This application will field information and update an SQLite database that is stored locally. If inventory
items reach a trigger level, the application runs shell commands to send a message using the attached Nova Hologram
hardware.

###Dependencies
SQLITE3
Java
Telnet (Mac/PC version only, not necessary on the Pi)