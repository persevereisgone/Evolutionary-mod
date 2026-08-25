param(
    [Parameter(Mandatory=$true)][string]$Method,
    [string]$ParamsFile = '',
    [string]$Params = '{}',
    [string]$OutFile = '',
    [string]$SessionFile = "$PSScriptRoot\bb_session.txt"
)

$ErrorActionPreference = 'Stop'
$uri = 'http://localhost:3000/bb-mcp'

# Load params from file if provided
if ($ParamsFile -and (Test-Path $ParamsFile)) {
    $Params = (Get-Content $ParamsFile -Raw).Trim()
}

function Invoke-McpRaw {
    param([int]$Id, [string]$Method, [string]$ParamsJson)
    $body = "{`"jsonrpc`":`"2.0`",`"id`":$Id,`"method`":`"$Method`",`"params`":$ParamsJson}"
    $headers = @{ Accept = 'application/json, text/event-stream' }
    if (Test-Path $SessionFile) {
        $sid = (Get-Content $SessionFile -Raw).Trim()
        if ($sid) { $headers['Mcp-Session-Id'] = $sid }
    }
    $r = Invoke-WebRequest -Uri $uri -Method Post -Body $body -ContentType 'application/json' -Headers $headers -UseBasicParsing -TimeoutSec 120
    if ($r.Headers['Mcp-Session-Id']) {
        Set-Content -Path $SessionFile -Value $r.Headers['Mcp-Session-Id'] -NoNewline
    }
    $content = $r.Content
    if ($content -match '^event:') {
        $content = ($content -split "`n" | Where-Object { $_ -match '^data: ' } | ForEach-Object { $_ -replace '^data: ', '' }) -join "`n"
    }
    return $content
}

# Ensure an active session
try {
    Invoke-McpRaw -Id 1 -Method 'initialize' -ParamsJson '{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"cursor-cli","version":"1.0"}}' | Out-Null
    Invoke-McpRaw -Id 0 -Method 'notifications/initialized' -ParamsJson '{}' | Out-Null
} catch {}

$generic = @('tools/list','tools/resources/list','prompts/list')
if ($generic -contains $Method) {
    $result = Invoke-McpRaw -Id 99 -Method $Method -ParamsJson $Params
} else {
    $paramsJson = "{`"name`":`"$Method`",`"arguments`":$Params}"
    $result = Invoke-McpRaw -Id 99 -Method 'tools/call' -ParamsJson $paramsJson
}

if ($OutFile) {
    $result | Out-File -FilePath $OutFile -Encoding utf8
    Write-Output "WROTE $OutFile ($(($result).Length) chars)"
} else {
    Write-Output $result
}