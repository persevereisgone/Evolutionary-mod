<#
.SYNOPSIS
Convert AI-generated item textures into Minecraft-ready 16x16 transparent PNGs.

.DESCRIPTION
For each target PNG in the item texture folder:
  1. Backup the raw source to docs/texture-sources/<name>.src.png
  2. Downscale to 512 (bicubic)
  3. Build a robust background model from border samples (median + outlier discard)
  4. Flood-fill background removal from the image borders
  5. Crop content bounding box, pad to square, downscale to 16x16
  6. Verify (16x16, corner alpha 0) and overwrite the texture

.PARAMETER Name
One or more texture registry names (without .png). If omitted, all PNG files
larger than 16px in the item texture folder are processed.

.PARAMETER Force
Reprocess files even if they are already 16x16.

.EXAMPLE
powershell -ExecutionPolicy Bypass -File tools/ai-texture-to-16x16.ps1

.EXAMPLE
powershell -ExecutionPolicy Bypass -File tools/ai-texture-to-16x16.ps1 attribute_essence_life rank_shard_normal
#>
param(
    [Parameter(Position = 0)]
    [string[]]$Name,
    [switch]$Force
)

Add-Type -AssemblyName System.Drawing

$ErrorActionPreference = "Stop"
$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$root = Split-Path -Parent $scriptRoot
$itemDir = Join-Path $root "src\main\resources\assets\evolutionary_mod\textures\item"
$backupDir = Join-Path $root "docs\texture-sources"
New-Item -ItemType Directory -Force -Path $backupDir | Out-Null
if (-not (Test-Path $itemDir)) { throw "Item texture folder not found: $itemDir" }

function Convert-ToArgb([System.Drawing.Bitmap]$bmp) {
    $out = New-Object System.Drawing.Bitmap($bmp.Width, $bmp.Height, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($out)
    $g.DrawImageUnscaled($bmp, 0, 0)
    $g.Dispose()
    $bmp.Dispose()
    return $out
}

function Lock-Data([System.Drawing.Bitmap]$bmp) {
    $rect = New-Object System.Drawing.Rectangle(0,0,$bmp.Width,$bmp.Height)
    $data = $bmp.LockBits($rect, [System.Drawing.Imaging.ImageLockMode]::ReadWrite, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $bytes = New-Object byte[] ($data.Stride * $data.Height)
    [System.Runtime.InteropServices.Marshal]::Copy($data.Scan0, $bytes, 0, $bytes.Length)
    return @($data, $bytes)
}

function Get-BgModel($bytes, $W, $H, $stride) {
    $rs = New-Object System.Collections.Generic.List[int]
    $gs = New-Object System.Collections.Generic.List[int]
    $bs = New-Object System.Collections.Generic.List[int]
    for ($x=0; $x -lt $W; $x += 6) {
        $p0 = $x*4; $p1 = ($H-1)*$stride + $x*4
        $bs.Add($bytes[$p0]); $gs.Add($bytes[$p0+1]); $rs.Add($bytes[$p0+2])
        $bs.Add($bytes[$p1]); $gs.Add($bytes[$p1+1]); $rs.Add($bytes[$p1+2])
    }
    for ($y=0; $y -lt $H; $y += 6) {
        $p0 = $y*$stride; $p1 = $y*$stride + ($W-1)*4
        $bs.Add($bytes[$p0]); $gs.Add($bytes[$p0+1]); $rs.Add($bytes[$p0+2])
        $bs.Add($bytes[$p1]); $gs.Add($bytes[$p1+1]); $rs.Add($bytes[$p1+2])
    }
    $n = $rs.Count
    $sr = New-Object int[] $n; $sg = New-Object int[] $n; $sb = New-Object int[] $n
    for ($i=0; $i -lt $n; $i++) { $sr[$i]=$rs[$i]; $sg[$i]=$gs[$i]; $sb[$i]=$bs[$i] }
    [Array]::Sort($sr); [Array]::Sort($sg); [Array]::Sort($sb)
    $medR = $sr[[int]($n/2)]; $medG = $sg[[int]($n/2)]; $medB = $sb[[int]($n/2)]

    $dr = New-Object System.Collections.Generic.List[int]
    $dg = New-Object System.Collections.Generic.List[int]
    $db = New-Object System.Collections.Generic.List[int]
    for ($i=0; $i -lt $n; $i++) {
        $dr.Add([math]::Abs($rs[$i]-$medR)); $dg.Add([math]::Abs($gs[$i]-$medG)); $db.Add([math]::Abs($bs[$i]-$medB))
    }
    $ar = $dr.ToArray(); $ag = $dg.ToArray(); $ab = $db.ToArray()
    [Array]::Sort($ar); [Array]::Sort($ag); [Array]::Sort($ab)
    $k = [math]::Max(1, [int]($n*0.03))
    $maxDevR = $ar[$n-1-$k]; $maxDevG = $ag[$n-1-$k]; $maxDevB = $ab[$n-1-$k]
    $tol = $maxDevR + $maxDevG + $maxDevB + 45
    return @($medR, $medG, $medB, $tol)
}

function Remove-Background([System.Drawing.Bitmap]$src) {
    $target = 512
    $dst = New-Object System.Drawing.Bitmap($target,$target,[System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($dst)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.DrawImage($src, 0, 0, $target, $target)
    $g.Dispose()
    $src.Dispose()
    $dst = Convert-ToArgb $dst

    $lk = Lock-Data $dst
    $data = $lk[0]; $bytes = $lk[1]
    $W = $dst.Width; $H = $dst.Height; $stride = $data.Stride

    $bg = Get-BgModel $bytes $W $H $stride
    $bgR = $bg[0]; $bgG = $bg[1]; $bgB = $bg[2]; $tol = $bg[3]
    Write-Host ("    bg=({0},{1},{2}) tol={3}" -f $bgR,$bgG,$bgB,$tol)

    $visited = New-Object byte[] ($W*$H)
    $queue = New-Object System.Collections.Generic.List[int]
    for ($x=0; $x -lt $W; $x++) { $queue.Add($x); $queue.Add((($H-1)*$W)+$x) }
    for ($y=0; $y -lt $H; $y++) { $queue.Add($y*$W); $queue.Add(($y*$W)+$W-1) }
    foreach ($idx in $queue) { $visited[$idx] = 1 }

    $head = 0
    while ($head -lt $queue.Count) {
        $idx = $queue[$head]; $head++
        $x = $idx % $W; $y = [math]::Floor($idx / $W)
        $p = $y*$stride + $x*4
        $bytes[$p+3] = 0
        if ($x -gt 0)   { $ni=$idx-1; if($visited[$ni]-eq 0){ $visited[$ni]=1; $np=$y*$stride+($x-1)*4; $d=[math]::Abs($bytes[$np+2]-$bgR)+[math]::Abs($bytes[$np+1]-$bgG)+[math]::Abs($bytes[$np]-$bgB); if($d -lt $tol){$queue.Add($ni)} } }
        if ($x -lt $W-1){ $ni=$idx+1; if($visited[$ni]-eq 0){ $visited[$ni]=1; $np=$y*$stride+($x+1)*4; $d=[math]::Abs($bytes[$np+2]-$bgR)+[math]::Abs($bytes[$np+1]-$bgG)+[math]::Abs($bytes[$np]-$bgB); if($d -lt $tol){$queue.Add($ni)} } }
        if ($y -gt 0)   { $ni=$idx-$W; if($visited[$ni]-eq 0){ $visited[$ni]=1; $np=($y-1)*$stride+$x*4; $d=[math]::Abs($bytes[$np+2]-$bgR)+[math]::Abs($bytes[$np+1]-$bgG)+[math]::Abs($bytes[$np]-$bgB); if($d -lt $tol){$queue.Add($ni)} } }
        if ($y -lt $H-1){ $ni=$idx+$W; if($visited[$ni]-eq 0){ $visited[$ni]=1; $np=($y+1)*$stride+$x*4; $d=[math]::Abs($bytes[$np+2]-$bgR)+[math]::Abs($bytes[$np+1]-$bgG)+[math]::Abs($bytes[$np]-$bgB); if($d -lt $tol){$queue.Add($ni)} } }
    }

    [System.Runtime.InteropServices.Marshal]::Copy($bytes, 0, $data.Scan0, $bytes.Length)
    $dst.UnlockBits($data)
    return $dst
}

function Get-ContentBBox($bmp) {
    $lk = Lock-Data $bmp
    $bytes = $lk[1]; $stride = $lk[0].Stride
    $W = $bmp.Width; $H = $bmp.Height
    $minX=$W; $minY=$H; $maxX=-1; $maxY=-1
    for ($y=0; $y -lt $H; $y++) {
        $row = $y * $stride
        for ($x=0; $x -lt $W; $x++) {
            if ($bytes[$row + $x*4 + 3] -gt 40) {
                if($x -lt $minX){$minX=$x}; if($x -gt $maxX){$maxX=$x}
                if($y -lt $minY){$minY=$y}; if($y -gt $maxY){$maxY=$y}
            }
        }
    }
    $bmp.UnlockBits($lk[0])
    return @($minX,$minY,$maxX,$maxY)
}

function Save-Resized($src, $path) {
    $bbox = Get-ContentBBox $src
    $crow = $bbox[2]-$bbox[0]+1; $dcol = $bbox[3]-$bbox[1]+1
    if ($crow -le 0 -or $dcol -le 0) { $src.Dispose(); return $false }
    $side = [math]::Max($crow,$dcol)
    $crop = New-Object System.Drawing.Bitmap($side,$side,[System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($crop)
    $g.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
    $dx = [math]::Floor(($side-$crow)/2); $dy = [math]::Floor(($side-$dcol)/2)
    $g.DrawImage($src, (New-Object System.Drawing.Rectangle($dx,$dy,$crow,$dcol)), (New-Object System.Drawing.Rectangle($bbox[0],$bbox[1],$crow,$dcol)), [System.Drawing.GraphicsUnit]::Pixel)
    $g.Dispose()
    $src.Dispose()

    $out = New-Object System.Drawing.Bitmap(16,16,[System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g2 = [System.Drawing.Graphics]::FromImage($out)
    $g2.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g2.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
    $g2.DrawImage($crop, 0, 0, 16, 16)
    $g2.Dispose()
    $crop.Dispose()
    $out.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $out.Dispose()
    return $true
}

function Get-AverageHue($bmp) {
    $sr=0.0;$sg=0.0;$sb=0.0;$cnt=0
    for ($y=0; $y -lt 16; $y++) { for ($x=0; $x -lt 16; $x++) {
        $c = $bmp.GetPixel($x,$y)
        if ($c.A -gt 128) { $sr += $c.R; $sg += $c.G; $sb += $c.B; $cnt++ }
    } }
    if ($cnt -eq 0) { return "EMPTY" }
    $mr=$sr/$cnt; $mg=$sg/$cnt; $mb=$sb/$cnt
    $max=[math]::Max($mr,[math]::Max($mg,$mb)); $min=[math]::Min($mr,[math]::Min($mg,$mb))
    if ($max -eq $min) { return "gray" }
    $d=$max-$min
    if ($max -eq $mr) { $h=((($mg-$mb)/$d) % 6)*60 } elseif ($max -eq $mg) { $h=(($mb-$mr)/$d+2)*60 } else { $h=(($mr-$mg)/$d+4)*60 }
    if ($h -lt 0) { $h += 360 }
    if ($h -lt 15 -or $h -ge 345) { return "red" }
    if ($h -lt 45) { return "orange" }
    if ($h -lt 70) { return "yellow" }
    if ($h -lt 160) { return "green" }
    if ($h -lt 200) { return "cyan" }
    if ($h -lt 255) { return "blue" }
    if ($h -lt 290) { return "purple" }
    if ($h -lt 330) { return "pink" }
    return "red"
}

# ============ resolve target names ============
if ($Name.Count -eq 0) {
    $Name = @(Get-ChildItem $itemDir -Filter *.png | Where-Object {
        $bmp = New-Object System.Drawing.Bitmap($_.FullName)
        $big = ($bmp.Width -gt 16 -or $bmp.Height -gt 16)
        $bmp.Dispose()
        $big
    } | ForEach-Object { $_.BaseName })
}

if ($Name.Count -eq 0) { Write-Host "Nothing to process (no target found)."; exit 0 }
Write-Host ("Targets: " + ($Name -join ", "))

$results = @()
foreach ($target in $Name) {
    $srcPath = Join-Path $itemDir ($target + ".png")
    if (-not (Test-Path $srcPath)) {
        Write-Host ("MISSING FILE: " + $target)
        $results += [pscustomobject]@{ Name=$target; Status="MISSING FILE"; Hue=""; Bytes=0 }
        continue
    }
    $bmp0 = New-Object System.Drawing.Bitmap($srcPath)
    $is16 = ($bmp0.Width -le 16 -and $bmp0.Height -le 16)
    $c0 = $bmp0.GetPixel(0,0)
    $alreadyTransparent = ($c0.A -eq 0)
    $bmp0.Dispose()

    if ($is16 -and -not $Force) {
        Write-Host ("SKIP (already 16x16): " + $target)
        $results += [pscustomobject]@{ Name=$target; Status="SKIP (16x16)"; Hue=""; Bytes=0 }
        continue
    }

    Copy-Item $srcPath (Join-Path $backupDir ($target + ".src.png")) -Force

    $bmp = New-Object System.Drawing.Bitmap($srcPath)
    if (-not $alreadyTransparent) { $bmp = Remove-Background $bmp }
    $ok = Save-Resized $bmp $srcPath

    if ($ok) {
        $fi = Get-Item $srcPath
        $v = New-Object System.Drawing.Bitmap($srcPath)
        $hue = Get-AverageHue $v
        $cornerA = $v.GetPixel(0,0).A
        $v.Dispose()
        $status = "OK"
        if ($cornerA -ne 0) { $status = "OK (cornerA=$cornerA)" }
        Write-Host ("OK: {0} -> {1} bytes ({2})" -f $target, $fi.Length, $hue)
        $results += [pscustomobject]@{ Name=$target; Status=$status; Hue=$hue; Bytes=$fi.Length }
    } else {
        Write-Host ("FAILED (empty content, file untouched): " + $target)
        $results += [pscustomobject]@{ Name=$target; Status="FAILED"; Hue=""; Bytes=0 }
    }
}

Write-Host ""
Write-Host "+---------------------------------+"
foreach ($r in $results) {
    Write-Host ("| {0,-32} | {1,-6} | {2,-8} |" -f $r.Name, $r.Status, $r.Hue)
}
Write-Host "+---------------------------------+"