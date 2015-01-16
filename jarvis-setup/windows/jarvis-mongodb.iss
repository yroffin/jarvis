; -- jarvis-mongodb.iss --
; To successfully run this installation and the program it installs,
; you must have a "x64" edition of Windows.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=JarvisMongoDB
AppVersion=1.0beta1
DefaultDirName={pf}\Jarvis\MongoDB
DefaultGroupName=Jarvis
UninstallDisplayIcon={app}\uninstall-jarvis-mongodb.exe
Compression=lzma2/ultra64
SolidCompression=yes
OutputDir=userdocs:jarvis-setup
ArchitecturesAllowed=x64
ArchitecturesInstallIn64BitMode=x64
OutputBaseFilename=setup-jarvis-mongodb-1.0beta2

[Files]
; mongodb
Source: "../../jarvis-core/jarvis-mongodb/target/mongodb-win32-x86_64/mongodb-win32-x86_64-2008plus-2.6.6/*"; DestDir: "{app}/mongodb"; Flags: recursesubdirs
; Win-bash (commons)
Source: "../../jarvis-core/jarvis-scripts/src/main/shell.w32-ix86/*"; DestDir: "{app}/scripts/shell.w32-ix86"; Flags: recursesubdirs
; Depends (commons)
Source: "../../jarvis-core/jarvis-scripts/src/main/depends22/*"; DestDir: "{app}/scripts/depends22"; Flags: recursesubdirs
; Scripts
Source: "../../jarvis-core/jarvis-scripts/src/main/jarvis-mongodb.sh"; DestDir: "{app}/scripts";
Source: "../../jarvis-core/jarvis-scripts/src/main/jarvis-mongodb-bootstrap.cmd"; DestDir: "{app}/scripts";

[Registry]
Root: HKCU; Subkey: "Software\Jarvis"; Flags: uninsdeletekeyifempty
Root: HKCU; Subkey: "Software\Jarvis\MongoDB"; Flags: uninsdeletekey
Root: HKLM; Subkey: "Software\Jarvis"; Flags: uninsdeletekeyifempty
Root: HKLM; Subkey: "Software\Jarvis\MongoDB"; Flags: uninsdeletekey
Root: HKLM; Subkey: "Software\Jarvis\MongoDB\Settings"; ValueType: string; ValueName: "InstallPath"; ValueData: "{app}"
