@echo off
COLOR 0C
title FandC-AuthServer Console
:start
echo Login Server.
echo.

java -server -Dfile.encoding=UTF-8 -Xmx64m -cp config;./../libs/* l2f.loginserver.AuthServer

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