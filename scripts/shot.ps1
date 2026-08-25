param(
    [Parameter(Mandatory=$true)][string]$ParamsFile,
    [Parameter(Mandatory=$true)][string]$OutPng
)

$s = Join-Path $PSScriptRoot 'bbmcp.ps1'
$res = powershell -NoProfile -ExecutionPolicy Bypass -File $s -Method 'capture_screenshot' -ParamsFile $ParamsFile 2>&1 | Out-String
$m = [regex]::Match($res, '"data":"([A-Za-z0-9+/=]+)"')
if ($m.Success) {
    $bytes = [Convert]::FromBase64String($m.Groups[1].Value)
    $dir = Split-Path $OutPng -Parent
    if (!(Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    [IO.File]::WriteAllBytes($OutPng, $bytes)
    Write-Host "Saved $OutPng ($($bytes.Length) bytes)"
} else {
    Write-Host "No image data found."
}