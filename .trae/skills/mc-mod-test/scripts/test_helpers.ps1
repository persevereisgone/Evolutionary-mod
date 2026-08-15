# MC Mod Test Helper Scripts (v3.1)
# 集成 UI 测试到完整测试流程

# 引入 UI 测试辅助脚本
$uiHelpersPath = ".trae/skills/mc-mod-test/scripts/ui_test_helpers.ps1"
if (Test-Path $uiHelpersPath) {
    . $uiHelpersPath
    Write-Host "[OK] UI test helpers loaded" -ForegroundColor Green
} else {
    Write-Host "[WARNING] UI helpers not found: $uiHelpersPath" -ForegroundColor Yellow
}

function Read-ModConfig {
    $ConfigPath = ".trae/skills/mc-mod-test/config/mod_structure.json"
    
    if (-not (Test-Path $ConfigPath)) {
        Write-Host "[ERROR] Config file not found: $ConfigPath" -ForegroundColor Red
        return $null
    }
    
    try {
        $configContent = Get-Content $ConfigPath -Raw -Encoding UTF8
        $config = $configContent | ConvertFrom-Json
        Write-Host "[OK] Config loaded successfully" -ForegroundColor Green
        return $config
    } catch {
        Write-Host "[ERROR] Failed to parse config: $_" -ForegroundColor Red
        return $null
    }
}

function Test-FileStructure {
    $config = Read-ModConfig
    if (-not $config) {
        return
    }

    Write-Host "`n=== File Structure Check ===" -ForegroundColor Cyan
    
    $javaSource = $config.project_structure.java_source
    $mainPackage = $config.project_structure.main_package
    $packagePath = $mainPackage.Replace(".", "/")
    $basePath = "$javaSource/$packagePath"

    foreach ($system in $config.test_targets.systems) {
        if (-not $system.enabled) {
            continue
        }

        Write-Host "`n[SCAN] $($system.name)" -ForegroundColor Yellow
        
        $systemPath = $basePath
        if ($system.package) {
            $systemPath = "$basePath/$($system.package.Replace(".", "/"))"
        }

        foreach ($file in $system.files) {
            $fullPath = "$systemPath/$file"
            $fullFilePath = "$PWD/$fullPath"
            
            if (Test-Path $fullFilePath) {
                Write-Host "   OK: $file" -ForegroundColor Green
            } else {
                Write-Host "   MISS: $file" -ForegroundColor Red
            }
        }
    }
}

function Start-UITest {
    $config = Read-ModConfig
    if (-not $config) {
        return
    }

    Write-Host "`n=== UI Test ===" -ForegroundColor Cyan
    
    $uiElements = $config.test_targets.ui_elements | Where-Object { $_.enabled }
    if (-not $uiElements -or $uiElements.Count -eq 0) {
        Write-Host "[SKIP] No enabled UI elements" -ForegroundColor Gray
        return
    }

    Write-Host "Found $($uiElements.Count) UI elements" -ForegroundColor Cyan
    foreach ($ui in $uiElements) {
        Write-Host "   - $($ui.name)" -ForegroundColor White
    }

    # Call full UI test (from ui_test_helpers.ps1)
    $uiTestResult = Invoke-UITestMain -ProjectRoot $PWD.Path -ConfigPath ".trae/skills/mc-mod-test/config/mod_structure.json"
    
    if ($uiTestResult) {
        Write-Host "[OK] UI test completed" -ForegroundColor Green
    } else {
        Write-Host "[WARNING] UI test returned no result" -ForegroundColor Yellow
    }
}

function Start-FullTest {
    Write-Host "=== Full Test Suite ===" -ForegroundColor Cyan

    Write-Host "`n[1/3] Building..." -ForegroundColor Yellow
    Write-Host "Running: .\gradlew.bat build --no-daemon -x test" -ForegroundColor Gray
    & .\gradlew.bat build --no-daemon -x test 2>&1
    
    Write-Host "`n[2/3] File check..." -ForegroundColor Yellow
    Test-FileStructure
    
    Write-Host "`n[3/3] UI test..." -ForegroundColor Yellow
    Start-UITest

    Write-Host "`n=== Test Complete ===" -ForegroundColor Green
}

function Start-ModularTest {
    <#
    .SYNOPSIS
    执行模块化测试（可选择特定模块）
    
    .DESCRIPTION
    根据指定的模块名称执行测试，支持 UI 测试集成
    #>
    param(
        [string[]]$Modules,
        [switch]$IncludeUI
    )

    Write-Host "=== Modular Test Suite ===" -ForegroundColor Cyan

    $config = Read-ModConfig
    if (-not $config) {
        return
    }

    # 1. 构建项目
    Write-Host "`n[1/3] Building..." -ForegroundColor Yellow
    Write-Host "Running: .\gradlew.bat build --no-daemon -x test" -ForegroundColor Gray
    & .\gradlew.bat build --no-daemon -x test 2>&1

    # 2. 文件结构检查
    Write-Host "`n[2/3] File check for selected modules..." -ForegroundColor Yellow
    
    $javaSource = $config.project_structure.java_source
    $mainPackage = $config.project_structure.main_package
    $packagePath = $mainPackage.Replace(".", "/")
    $basePath = "$javaSource/$packagePath"

    foreach ($moduleName in $Modules) {
        $system = $config.test_targets.systems | Where-Object { $_.name -eq $moduleName }
        if (-not $system) {
            Write-Host "[WARNING] Module not found: $moduleName" -ForegroundColor Yellow
            continue
        }
        
        if (-not $system.enabled) {
            Write-Host "[SKIP] Module disabled: $moduleName" -ForegroundColor Gray
            continue
        }

        Write-Host "`n[SCAN] $($system.name)" -ForegroundColor Yellow
        
        $systemPath = $basePath
        if ($system.package) {
            $systemPath = "$basePath/$($system.package.Replace(".", "/"))"
        }

        foreach ($file in $system.files) {
            $fullPath = "$systemPath/$file"
            $fullFilePath = "$PWD/$fullPath"
            
            if (Test-Path $fullFilePath) {
                Write-Host "   OK: $file" -ForegroundColor Green
            } else {
                Write-Host "   MISS: $file" -ForegroundColor Red
            }
        }
    }

    # 3. UI 测试（如果包含 UI 模块或明确指定）
    $hasUIElements = $config.test_targets.ui_elements | Where-Object { $_.enabled }
    $hasUIModule = $Modules | Where-Object { $_ -match "客户端|UI|界面" }

    if (($IncludeUI) -or $hasUIModule -or ($Modules.Count -eq 0 -and $hasUIElements)) {
        Write-Host "`n[3/3] UI test..." -ForegroundColor Yellow
        Start-UITest
    } else {
        Write-Host "`n[3/3] UI test skipped (no UI modules selected)" -ForegroundColor Gray
    }

    Write-Host "`n=== Modular Test Complete ===" -ForegroundColor Green
}

Write-Host "MC Mod Test Helper (v3.1)" -ForegroundColor Cyan
Write-Host "Available commands:" -ForegroundColor Yellow
Write-Host "  Test-FileStructure"
Write-Host "  Start-UITest"
Write-Host "  Start-FullTest"
Write-Host "  Start-ModularTest -Modules @('模块名称') [-IncludeUI]"
