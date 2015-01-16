; -- jarvis.iss --
; To successfully run this installation and the program it installs,
; you must have a "x64" edition of Windows.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=JarvisServer
AppVersion=1.0beta2
DefaultDirName={pf}\Jarvis\Server
DefaultGroupName=Jarvis
UninstallDisplayIcon={app}\uninstall-jarvis-server.exe
Compression=lzma2/ultra64
SolidCompression=yes
OutputDir=userdocs:jarvis-setup
ArchitecturesAllowed=x64
ArchitecturesInstallIn64BitMode=x64
OutputBaseFilename=setup-jarvis-server-1.0beta2

[Files]
; node.exe
Source: "../../jarvis-core/jarvis-nodejs/node/*"; DestDir: "{app}/node"; Flags: recursesubdirs
; TODO blammo.xml injection
; TODO cert https injection
; NodeJS
Source: "../../jarvis-core/jarvis-nodejs/src/main/nodejs/*"; DestDir: "{app}/nodejs"; Flags: recursesubdirs
; Win-bash (commons)
Source: "../../jarvis-core/jarvis-scripts/src/main/shell.w32-ix86/*"; DestDir: "{app}/scripts/shell.w32-ix86"; Flags: recursesubdirs
; Depends (commons)
Source: "../../jarvis-core/jarvis-scripts/src/main/depends22/*"; DestDir: "{app}/scripts/depends22"; Flags: recursesubdirs
; Scripts
Source: "../../jarvis-core/jarvis-scripts/src/main/jarvis.sh"; DestDir: "{app}/scripts";
Source: "../../jarvis-core/jarvis-scripts/src/main/jarvis-bootstrap.cmd"; DestDir: "{app}/scripts";

[Registry]
Root: HKCU; Subkey: "Software\Jarvis"; Flags: uninsdeletekeyifempty
Root: HKCU; Subkey: "Software\Jarvis\Server"; Flags: uninsdeletekey
Root: HKLM; Subkey: "Software\Jarvis"; Flags: uninsdeletekeyifempty
Root: HKLM; Subkey: "Software\Jarvis\Server"; Flags: uninsdeletekey
Root: HKLM; Subkey: "Software\Jarvis\Server\Settings"; ValueType: string; ValueName: "InstallPath"; ValueData: "{app}"
