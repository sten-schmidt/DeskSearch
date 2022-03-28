$startPath = Get-Location
$descsearchInstallPath = ".\install"

Set-Location -Path $descsearchInstallPath

$arg1, $argx = $args

if ('server','search','searchgui', 'words', 'setup', 'compact', 'compress', 'index', 'reindex' -contains $arg1) {
    java -jar DeskSearch.jar $args

} else {
    java -jar DeskSearch.jar search $args
}

Set-Location -Path $startPath
