; -- jarvis.iss --
; To successfully run this installation and the program it installs,
; you must have a "x64" edition of Windows.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=JarvisVoice
AppVersion=1.0beta2
DefaultDirName={pf}\Jarvis\Voice
DefaultGroupName=Jarvis
UninstallDisplayIcon={app}\uninstall-jarvis-voice.exe
Compression=lzma2/ultra64
SolidCompression=yes
OutputDir=userdocs:jarvis-setup
ArchitecturesAllowed=x64
ArchitecturesInstallIn64BitMode=x64
OutputBaseFilename=setup-jarvis-voice-1.0beta2

[Files]
; voice
Source: "../../jarvis-core/jarvis-voice/target/jarvis-voice-0.0.1-SNAPSHOT-jar-with-dependencies.jar"; DestDir: "{app}/java"; DestName: "jarvis-voice-0.0.1-SNAPSHOT.jar"
; Win-bash (commons)
Source: "../../jarvis-core/jarvis-scripts/src/main/shell.w32-ix86/*"; DestDir: "{app}/scripts/shell.w32-ix86"; Flags: recursesubdirs
; Depends (commons)
Source: "../../jarvis-core/jarvis-scripts/src/main/depends22/*"; DestDir: "{app}/scripts/depends22"; Flags: recursesubdirs
; Scripts
Source: "../../jarvis-core/jarvis-scripts/src/main/jarvis-voice.sh"; DestDir: "{app}/scripts";
Source: "../../jarvis-core/jarvis-scripts/src/main/jarvis-voice-bootstrap.cmd"; DestDir: "{app}/scripts";

[Registry]
Root: HKCU; Subkey: "Software\Jarvis"; Flags: uninsdeletekeyifempty
Root: HKCU; Subkey: "Software\Jarvis\Voice"; Flags: uninsdeletekey
Root: HKLM; Subkey: "Software\Jarvis"; Flags: uninsdeletekeyifempty
Root: HKLM; Subkey: "Software\Jarvis\Voice"; Flags: uninsdeletekey
Root: HKLM; Subkey: "Software\Jarvis\Voice\Settings"; ValueType: string; ValueName: "InstallPath"; ValueData: "{app}"
