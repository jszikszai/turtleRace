$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

# Requires a Windows JDK 21+ with javac, jar and jpackage on PATH.
foreach ($tool in @('javac', 'jar', 'jpackage')) {
    Get-Command $tool -ErrorAction Stop | Out-Null
}

$buildId = Get-Date -Format 'yyyyMMdd-HHmmss-fff'
$buildDir = Join-Path $PSScriptRoot "build\$buildId"
$classesDir = Join-Path $buildDir 'classes'
$inputDir = Join-Path $buildDir 'input'
$outputDir = Join-Path $PSScriptRoot "dist\$buildId"
New-Item -ItemType Directory -Path $classesDir, $inputDir, $outputDir | Out-Null

& javac -encoding UTF-8 -Xlint:all -d $classesDir (Join-Path $PSScriptRoot 'TurtleRace.java')
if ($LASTEXITCODE -ne 0) { throw 'Java compilation failed.' }

$jarPath = Join-Path $inputDir 'TurtleRace.jar'
& jar --create --file $jarPath --main-class TurtleRace -C $classesDir .
if ($LASTEXITCODE -ne 0) { throw 'JAR creation failed.' }

& jpackage --type app-image --name TurtleRace --app-version 1.0.0 `
    --vendor jszikszai --input $inputDir --main-jar TurtleRace.jar `
    --main-class TurtleRace --add-modules java.desktop --dest $outputDir
if ($LASTEXITCODE -ne 0) { throw 'Windows packaging failed.' }

$appDir = Join-Path $outputDir 'TurtleRace'
Copy-Item -LiteralPath (Join-Path $PSScriptRoot 'RUNNING.txt') -Destination $appDir
Copy-Item -LiteralPath $jarPath -Destination $outputDir
$zipPath = Join-Path $outputDir 'TurtleRace-Windows.zip'
Compress-Archive -LiteralPath $appDir -DestinationPath $zipPath
Write-Output "Windows application: $appDir\TurtleRace.exe"
Write-Output "Shareable archive: $zipPath"
Write-Output "Standalone JAR (requires Java): $outputDir\TurtleRace.jar"
