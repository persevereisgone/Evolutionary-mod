# MC Mod UI Test Helper Scripts (v1.1)
# These scripts are used for UI automation testing

<#
Usage:
1. Ensure config file exists: .trae/skills/mc-mod-test/config/mod_structure.json
2. ui_elements in config defines the UI to test
3. Scripts will automatically read config and execute UI tests
#>

## Environment Check

function Test-UIEnvironment {
    <#
    .SYNOPSIS
    Check if UI test environment is ready

    .DESCRIPTION
    Check if game log files, screenshot directories, etc. exist
    #>
    param(
        [string]$ProjectRoot = $PWD.Path
    )

    Write-Host "======================================" -ForegroundColor Cyan
    Write-Host "UI Test Environment Check" -ForegroundColor Cyan
    Write-Host "======================================" -ForegroundColor Cyan

    $issues = @()

    # Check game log directory
    $logPath = "$ProjectRoot\logs\latest.log"
    if (-not (Test-Path $logPath)) {
        Write-Host "WARNING: Game log file not found (game may not have been run)" -ForegroundColor Yellow
        $issues += "Game log file not found"
    } else {
        Write-Host "OK: Game log file exists: logs\latest.log" -ForegroundColor Green
    }

    # Check screenshot directory
    $screenshotDir = "$ProjectRoot\screenshots"
    if (-not (Test-Path $screenshotDir)) {
        New-Item -ItemType Directory -Path $screenshotDir -Force | Out-Null
        Write-Host "OK: Screenshot directory created: screenshots\" -ForegroundColor Green
    } else {
        Write-Host "OK: Screenshot directory exists: screenshots\" -ForegroundColor Green
    }

    # Check gradlew.bat
    $gradlewBat = "$ProjectRoot\gradlew.bat"
    if (-not (Test-Path $gradlewBat)) {
        Write-Host "ERROR: gradlew.bat not found" -ForegroundColor Red
        $issues += "gradlew.bat not found"
    } else {
        Write-Host "OK: gradlew.bat exists" -ForegroundColor Green
    }

    Write-Host ""
    Write-Host "======================================" -ForegroundColor Cyan
    if ($issues.Count -eq 0) {
        Write-Host "OK: Environment check passed" -ForegroundColor Green
        return $true
    } else {
        Write-Host "WARNING: Found $($issues.Count) issues" -ForegroundColor Yellow
        return $false
    }
    Write-Host "======================================" -ForegroundColor Cyan
}

## Wait for Game Start

function Wait-ForGameStart {
    <#
    .SYNOPSIS
    Wait for game to fully start

    .DESCRIPTION
    Parse game log and wait for game to finish loading
    #>
    param(
        [string]$ProjectRoot = $PWD.Path,
        [int]$TimeoutSeconds = 300
    )

    $logPath = "$ProjectRoot\logs\latest.log"
    $startTime = Get-Date
    $lastSize = 0

    Write-Host ""
    Write-Host "Waiting for game to start..." -ForegroundColor Yellow

    while ((Get-Date) -lt $startTime.AddSeconds($TimeoutSeconds)) {
        if (Test-Path $logPath) {
            $logContent = Get-Content $logPath -Tail 50 -ErrorAction SilentlyContinue
            $logText = $logContent -join "`n"

            # Detect game startup completion
            if ($logText -match "Minecraft .* started on port" -or
                $logText -match "Done \(.*\)\. For help, type ""help""" -or
                $logText -match "forge.*initialized") {

                Write-Host "OK: Game started successfully" -ForegroundColor Green
                Start-Sleep -Seconds 3
                return $true
            }
        }

        # Show waiting status every 10 seconds
        $elapsed = ((Get-Date) - $startTime).Seconds
        if ($elapsed % 10 -eq 0 -and $elapsed -gt 0) {
            Write-Host "   Waiting for $elapsed seconds..." -ForegroundColor Gray
        }

        Start-Sleep -Seconds 2
    }

    Write-Host "ERROR: Timeout waiting for game to start ($TimeoutSeconds seconds)" -ForegroundColor Red
    return $false
}

## Send Key Press

function Send-KeyPress {
    <#
    .SYNOPSIS
    Send key press

    .DESCRIPTION
    Use PowerShell to send key commands to active window
    #>
    param(
        [string]$Key = "K"
    )

    Write-Host "   Sending key: $Key" -ForegroundColor Gray

    try {
        $wshell = New-Object -ComObject wscript.shell
        $wshell.AppActivate("Minecraft") | Out-Null
        Start-Sleep -Milliseconds 100
        $wshell.SendKeys($Key)
        Start-Sleep -Milliseconds 500
    } catch {
        Write-Host "   WARNING: Failed to send key (may need manual operation)" -ForegroundColor Yellow
    }
}

## Capture Game Screenshot

function Capture-GameScreenshot {
    <#
    .SYNOPSIS
    Capture game window

    .DESCRIPTION
    Use PowerShell to capture screenshot of specified window
    #>
    param(
        [string]$WindowTitle = "Minecraft",
        [string]$OutputPath
    )

    Add-Type -AssemblyName System.Windows.Forms
    Add-Type -AssemblyName System.Drawing

    Write-Host "   Capturing window: $WindowTitle" -ForegroundColor Gray

    try {
        # Find window
        $hwnd = [System.Diagnostics.Process]::GetCurrentProcess().MainWindowHandle

        if ($hwnd -eq [IntPtr]::Zero) {
            # If current window not found, try to find by process name
            $mcProcesses = Get-Process | Where-Object { $_.MainWindowTitle -like "*Minecraft*" }
            if ($mcProcesses) {
                $hwnd = $mcProcesses[0].MainWindowHandle
            }
        }

        if ($hwnd -eq [IntPtr]::Zero) {
            Write-Host "   WARNING: Cannot find Minecraft window" -ForegroundColor Yellow
            return $false
        }

        # Get window position and size
        $rect = New-Object System.Windows.Forms.Rectangle
        $windowHandle = [System.Windows.Forms.NativeMethods]::GetWindowRect($hwnd, [ref]$rect)

        if ($windowHandle) {
            $width = $rect.Right - $rect.Left
            $height = $rect.Bottom - $rect.Top

            # Create screenshot
            $bitmap = New-Object System.Drawing.Bitmap($width, $height)
            $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
            $graphics.CopyFromScreen($rect.Left, $rect.Top, 0, 0, $bitmap.Size)

            # Save screenshot
            $bitmap.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Png)
            $graphics.Dispose()
            $bitmap.Dispose()

            Write-Host "   OK: Screenshot saved: $OutputPath" -ForegroundColor Green
            return $true
        }
    } catch {
        Write-Host "   WARNING: Screenshot failed: $_" -ForegroundColor Yellow
    }

    return $false
}

## Simple Screenshot Method

function Capture-ScreenshotSimple {
    <#
    .SYNOPSIS
    Simple screenshot method

    .DESCRIPTION
    Use Screen clipping or PowerShell method to capture screenshot
    #>
    param(
        [string]$OutputPath
    )

    Write-Host "   Trying simple screenshot method..." -ForegroundColor Gray

    try {
        Add-Type -AssemblyName System.Windows.Forms
        Add-Type -AssemblyName System.Drawing

        # Get screen size
        $screen = [System.Windows.Forms.Screen]::PrimaryScreen
        $bounds = $screen.Bounds
        $width = $bounds.Width
        $height = $bounds.Height

        # Create full screen screenshot
        $bitmap = New-Object System.Drawing.Bitmap($width, $height)
        $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
        $graphics.CopyFromScreen($bounds.Location, [System.Drawing.Point]::Empty, $bounds.Size)

        # Save screenshot
        $bitmap.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Png)
        $graphics.Dispose()
        $bitmap.Dispose()

        Write-Host "   OK: Screenshot saved: $OutputPath" -ForegroundColor Green
        return $true
    } catch {
        Write-Host "   WARNING: Screenshot failed: $_" -ForegroundColor Yellow
        return $false
    }
}

## Main UI Test Function

function Invoke-UITestMain {
    <#
    .SYNOPSIS
    Execute UI automation test

    .DESCRIPTION
    Read UI elements from config, start game, capture screenshots and generate report
    #>
    param(
        [string]$ProjectRoot = $PWD.Path,
        [string]$ConfigPath = ".trae/skills/mc-mod-test/config/mod_structure.json",
        [switch]$SkipBuild
    )

    $ErrorActionPreference = "Continue"

    Write-Host "======================================" -ForegroundColor Cyan
    Write-Host "MC Mod UI Automation Test" -ForegroundColor Cyan
    Write-Host "======================================" -ForegroundColor Cyan

    # 1. Read config
    Write-Host ""
    Write-Host "[Step 1/5] Reading config..." -ForegroundColor Yellow

    if (-not (Test-Path $ConfigPath)) {
        Write-Host "ERROR: Config file not found: $ConfigPath" -ForegroundColor Red
        return $null
    }

    try {
        $configContent = Get-Content $ConfigPath -Raw -Encoding UTF8
        $config = $configContent | ConvertFrom-Json
        Write-Host "OK: Config file read successfully" -ForegroundColor Green
    } catch {
        Write-Host "ERROR: Failed to parse config: $_" -ForegroundColor Red
        return $null
    }

    # Get UI elements list
    $uiElements = $config.test_targets.ui_elements
    if (-not $uiElements -or $uiElements.Count -eq 0) {
        Write-Host "WARNING: No UI elements defined in config" -ForegroundColor Yellow
        return $null
    }

    # Filter enabled UI
    $enabledUIs = $uiElements | Where-Object { $_.enabled -eq $true }
    if ($enabledUIs.Count -eq 0) {
        Write-Host "WARNING: No UI elements enabled" -ForegroundColor Yellow
        return $null
    }

    Write-Host "Found $($enabledUIs.Count) enabled UI elements" -ForegroundColor Cyan

    # 2. Environment check
    Write-Host ""
    Write-Host "[Step 2/5] Checking environment..." -ForegroundColor Yellow
    $envReady = Test-UIEnvironment -ProjectRoot $ProjectRoot
    if (-not $envReady) {
        Write-Host "WARNING: Environment check not fully passed, but continuing..." -ForegroundColor Yellow
    }

    # 3. Create screenshot directory
    Write-Host ""
    Write-Host "[Step 3/5] Preparing screenshot directory..." -ForegroundColor Yellow
    $dateStr = Get-Date -Format "yyyy-MM-dd"
    $timeStr = Get-Date -Format "HHmmss"
    $screenshotDir = "$ProjectRoot\screenshots\$dateStr"
    $reportDir = "$ProjectRoot\docs\ui_test_reports"

    if (-not (Test-Path $screenshotDir)) {
        New-Item -ItemType Directory -Path $screenshotDir -Force | Out-Null
    }
    if (-not (Test-Path $reportDir)) {
        New-Item -ItemType Directory -Path $reportDir -Force | Out-Null
    }

    Write-Host "Screenshot directory: screenshots\$dateStr" -ForegroundColor Cyan
    Write-Host "Report directory: docs\ui_test_reports" -ForegroundColor Cyan

    # 4. Start client (if not running)
    $clientRunning = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object {
        $_.MainWindowTitle -like "*Minecraft*"
    }

    if (-not $clientRunning -and -not $SkipBuild) {
        Write-Host ""
        Write-Host "[Step 4/5] Starting game client..." -ForegroundColor Yellow
        Write-Host "WARNING: Please open target UI in game after startup" -ForegroundColor Yellow
        Write-Host "WARNING: Press Ctrl+C to stop test" -ForegroundColor Yellow

        # Start client using Start-Process for better window handling
        Set-Location $ProjectRoot
        $gradlewPath = ".\gradlew.bat"
        
        Write-Host "Running: $gradlewPath runClient --no-daemon" -ForegroundColor Gray
        
        # Start process with visible window
        $processInfo = New-Object System.Diagnostics.ProcessStartInfo
        $processInfo.FileName = $gradlewPath
        $processInfo.Arguments = "runClient --no-daemon"
        $processInfo.WorkingDirectory = $ProjectRoot
        $processInfo.UseShellExecute = $true
        $processInfo.WindowStyle = [System.Diagnostics.ProcessWindowStyle]::Normal
        
        $clientProcess = [System.Diagnostics.Process]::Start($processInfo)
        
        Write-Host "Game client started with PID: $($clientProcess.Id)" -ForegroundColor Green
        
        # Wait a moment and check if process is still running
        Start-Sleep -Seconds 5
        
        # Check if process is still alive
        if (-not $clientProcess.HasExited) {
            Write-Host "OK: Process is running, waiting for game to load..." -ForegroundColor Green
            
            # Wait for game to start
            $gameStarted = Wait-ForGameStart -ProjectRoot $ProjectRoot -TimeoutSeconds 300
            
            if (-not $gameStarted) {
                Write-Host "ERROR: Game startup timeout" -ForegroundColor Red
                $processExited = $false
            }
        } else {
            Write-Host "ERROR: Game process exited immediately with code: $($clientProcess.ExitCode)" -ForegroundColor Red
            $processExited = $true
        }
        
        # If game didn't start properly, prompt for manual startup
        if ($processExited -or (-not $gameStarted)) {
            Write-Host ""
            Write-Host "======================================" -ForegroundColor Yellow
            Write-Host "Manual Startup Required" -ForegroundColor Yellow
            Write-Host "======================================" -ForegroundColor Yellow
            Write-Host "The game client failed to start automatically." -ForegroundColor White
            Write-Host "Please manually start the game using:" -ForegroundColor White
            Write-Host "  .\gradlew.bat runClient --no-daemon" -ForegroundColor Cyan
            Write-Host ""
            Write-Host "Press Enter when you have started the game and loaded into a world..." -ForegroundColor Yellow
            
            # Wait for user input
            Read-Host
            
            # Check again if game is running
            $clientRunning = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object {
                $_.MainWindowTitle -like "*Minecraft*"
            }
            
            if (-not $clientRunning) {
                Write-Host "ERROR: Still cannot detect running Minecraft game" -ForegroundColor Red
                Write-Host "UI test cannot proceed without running game" -ForegroundColor Red
                return $null
            }
            
            Write-Host "OK: Detected Minecraft is now running" -ForegroundColor Green
        }
    } elseif ($clientRunning) {
        Write-Host "OK: Detected Minecraft is already running" -ForegroundColor Green
    } else {
        Write-Host "WARNING: Skipping game startup (using -SkipBuild)" -ForegroundColor Yellow
    }

    # 5. Capture screenshots and generate report
    Write-Host ""
    Write-Host "[Step 5/5] Executing UI screenshots..." -ForegroundColor Yellow

    $screenshotResults = @()
    $reportTime = Get-Date -Format "yyyy-MM-dd HH:mm:ss"

    foreach ($ui in $enabledUIs) {
        Write-Host ""
        Write-Host "Testing UI: $($ui.name)" -ForegroundColor Cyan

        # Generate screenshot filename
        $safeName = $ui.name -replace '[^\w]', '_'
        $screenshotFile = "$screenshotDir\${safeName}_${timeStr}.png"

        # Send key press (if configured)
        if ($ui.open_key -and $ui.open_key -ne "NONE") {
            Send-KeyPress -Key $ui.open_key
            Start-Sleep -Seconds 2
        }

        # Capture screenshot
        Start-Sleep -Seconds 1
        $screenshotSuccess = Capture-ScreenshotSimple -OutputPath $screenshotFile

        # Record result
        $result = [PSCustomObject]@{
            UIName = $ui.name
            ScreenClass = $ui.screen_class
            MenuClass = $ui.menu_class
            ScreenshotPath = $screenshotFile
            ScreenshotSuccess = $screenshotSuccess
            OpenKey = if ($ui.open_key) { $ui.open_key } else { "N/A" }
        }
        $screenshotResults += $result

        if ($screenshotSuccess) {
            Write-Host "   OK: $($ui.name) screenshot captured" -ForegroundColor Green
        } else {
            Write-Host "   WARNING: $($ui.name) screenshot failed" -ForegroundColor Yellow
        }

        Start-Sleep -Seconds 1
    }

    # Generate UI test report
    Write-Host ""
    Write-Host "Generating UI test report..." -ForegroundColor Yellow

    $reportFile = "$reportDir\UI_TEST_REPORT_$($dateStr)_${timeStr}.md"

    # Build report content
    $uiTableRows = ($screenshotResults | ForEach-Object {
        $status = if ($_.ScreenshotSuccess) { 'OK' } else { 'FAILED' }
        "| $($_.UIName) | $($_.ScreenClass) | $($_.MenuClass) | $status | Pending |"
    }) -join "`n"

    $uiDetails = ""
    $i = 1
    foreach ($result in $screenshotResults) {
        $uiDetails += @"

### $i. $($result.UIName)

**Screen Class**: $($result.ScreenClass)
**Menu Class**: $($result.MenuClass)
**Open Key**: $($result.OpenKey)

**Screenshot Path**: ``$($result.ScreenshotPath.Replace($ProjectRoot, '.'))``

**Manual Checklist**:
- [ ] UI displays correctly
- [ ] UI elements positioned correctly
- [ ] Text is readable
- [ ] Buttons/interactions work

**Notes**: _______________________________________

---
"@
        $i++
    }

    $report = @"
# UI Test Report

## Test Information

| Item | Value |
|------|------|
| Test Date | $reportTime |
| Mod Name | $($config.mod_name) |
| Game Version | $($config.minecraft_version) |
| UI Test Count | $($screenshotResults.Count) |

---

## UI Test Results

| UI Name | Screen Class | Menu Class | Status | Manual Review |
|---------|-----------|---------|----------|----------|
$($uiTableRows)

---

## Screenshot List

$($uiDetails)

## Manual Review Conclusion

### Overall Assessment

| UI | Test Result | Issues | Priority |
|----|----------|----------|--------|
$(
    ($screenshotResults | ForEach-Object {
        $testResult = if ($_.ScreenshotSuccess) { 'OK' } else { 'FAILED' }
        "| $($_.UIName) | $testResult | - | P2 |"
    }) -join "`n"
)

### Summary

**UI Test Status**: $(if (($screenshotResults | Where-Object { -not $_.ScreenshotSuccess }).Count -eq 0) { 'All completed' } else { 'Partially failed' })

**Next Steps**:
1. Open the screenshot path above and manually review each UI
2. Determine if modifications are needed based on review
3. Edit corresponding Screen class files if necessary

---

## Screenshot Location

All screenshots saved to: `screenshots\$dateStr\`

---

*Report generated by MC Mod Test Skill*
*Generated: $reportTime*
"@

    $report | Out-File -FilePath $reportFile -Encoding UTF8

    Write-Host ""
    Write-Host "======================================" -ForegroundColor Cyan
    Write-Host "OK: UI Test Complete" -ForegroundColor Green
    Write-Host "Report generated: $reportFile" -ForegroundColor White
    Write-Host "Screenshot directory: $screenshotDir" -ForegroundColor White
    Write-Host "======================================" -ForegroundColor Cyan

    # Return result object
    return [PSCustomObject]@{
        Config = $config
        UIResults = $screenshotResults
        ReportFile = $reportFile
        ScreenshotDir = $screenshotDir
    }
}

## Quick UI Screenshot (without starting client)

function Start-QuickUIScreenshot {
    <#
    .SYNOPSIS
    Quick screenshot (assumes game is already running)

    .DESCRIPTION
    Directly capture screenshot of running game window
    #>
    param(
        [string]$ProjectRoot = $PWD.Path,
        [string]$OutputName = "quick_screenshot"
    )

    $dateStr = Get-Date -Format "yyyy-MM-dd"
    $timeStr = Get-Date -Format "HHmmss"
    $screenshotDir = "$ProjectRoot\screenshots\$dateStr"

    if (-not (Test-Path $screenshotDir)) {
        New-Item -ItemType Directory -Path $screenshotDir -Force | Out-Null
    }

    $screenshotFile = "$screenshotDir\${OutputName}_${timeStr}.png"

    Write-Host "Capturing quick screenshot..." -ForegroundColor Yellow
    $success = Capture-ScreenshotSimple -OutputPath $screenshotFile

    if ($success) {
        Write-Host "OK: Screenshot saved: $screenshotFile" -ForegroundColor Green
        return $screenshotFile
    } else {
        Write-Host "ERROR: Screenshot failed" -ForegroundColor Red
        return $null
    }
}

## Usage Examples

<#
# Check UI test environment
Test-UIEnvironment

# Execute full UI test (start client and capture)
Invoke-UITestMain

# Execute UI test (assumes game is running)
Invoke-UITestMain -SkipBuild

# Quick screenshot (game already running)
Start-QuickUIScreenshot -OutputName "test_ui"
#>

Write-Host ""
Write-Host "MC Mod UI Test Helper Scripts (v1.1)" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Available commands:" -ForegroundColor Yellow
Write-Host "  - Test-UIEnvironment      : Check UI test environment" -ForegroundColor White
Write-Host "  - Wait-ForGameStart       : Wait for game to start" -ForegroundColor White
Write-Host "  - Invoke-UITestMain       : Execute full UI test" -ForegroundColor White
Write-Host "  - Start-QuickUIScreenshot : Quick screenshot (game running)" -ForegroundColor White
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Config file: .trae/skills/mc-mod-test/config/mod_structure.json" -ForegroundColor Cyan
