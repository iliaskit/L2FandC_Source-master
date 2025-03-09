@mode con:cols=150 lines=500
@echo off
COLOR 0C
title FandC-GameServer Console
:start
echo Starting GameServer.
echo.

java -server -Dfile.encoding=UTF-8 -Xms2G -cp config;./../libs/* l2f.gameserver.GameServer

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Server restarted ...
echo.
goto start
:error
echo.
echo Server terminated abnormaly ...
echo.
:end
echo.
echo Server terminated ...
echo.

pause
