; -- jarvis.iss --
; To successfully run this installation and the program it installs,
; you must have a "x64" edition of Windows.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=JarvisEar
AppVersion=1.0beta2
DefaultDirName={pf}\Jarvis\Ear
DefaultGroupName=Jarvis
UninstallDisplayIcon={app}\uninstall-jarvis-ear.exe
Compression=lzma2/ultra64
SolidCompression=yes
OutputDir=userdocs:jarvis-setup
ArchitecturesAllowed=x64
ArchitecturesInstallIn64BitMode=x64
OutputBaseFilename=setup-jarvis-ear-1.0beta2

[Files]
; Windows clients
Source: "M:/Google Drive/com.github.yroffin/jarvis/model/*"; DestDir: "{app}/model"; Flags: recursesubdirs
Source: "../../jarvis-windows/WindowsJarvisMicrophone/bin/x86/Release/*.exe"; DestDir: "{app}/windows"
Source: "../../jarvis-windows/WindowsJarvisMicrophone/bin/x86/Release/*.dll"; DestDir: "{app}/windows"
; msvcrt120
Source: "C:\Windows\SysWOW64\msvcr120.dll"; DestDir: "{app}/windows"
Source: "C:\Windows\SysWOW64\msvcr120d.dll"; DestDir: "{app}/windows"
; Win-bash
Source: "../../jarvis-core/jarvis-scripts/src/main/shell.w32-ix86/*"; DestDir: "{app}/scripts/shell.w32-ix86"; Flags: recursesubdirs
; Depends
Source: "../../jarvis-core/jarvis-scripts/src/main/depends22/*"; DestDir: "{app}/scripts/depends22"; Flags: recursesubdirs
; Scripts
Source: "../../jarvis-core/jarvis-scripts/src/main/jarvis-ear.sh"; DestDir: "{app}/scripts";
Source: "../../jarvis-core/jarvis-scripts/src/main/jarvis-ear-bootstrap.cmd"; DestDir: "{app}/scripts";

[Registry]
Root: HKCU; Subkey: "Software\Jarvis"; Flags: uninsdeletekeyifempty
Root: HKCU; Subkey: "Software\Jarvis\Ear"; Flags: uninsdeletekey
Root: HKLM; Subkey: "Software\Jarvis"; Flags: uninsdeletekeyifempty
Root: HKLM; Subkey: "Software\Jarvis\Ear"; Flags: uninsdeletekey
Root: HKLM; Subkey: "Software\Jarvis\Ear\Settings"; ValueType: string; ValueName: "InstallPath"; ValueData: "{app}"
