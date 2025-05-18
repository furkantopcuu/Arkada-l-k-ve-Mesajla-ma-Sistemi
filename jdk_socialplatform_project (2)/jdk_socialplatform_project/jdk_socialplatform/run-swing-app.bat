@echo off
REM Sosyal Platform uygulama başlatıcı
echo Sosyal Platform uygulaması başlatılıyor...
echo.

REM Java sürümünü kontrol et
java -version 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Java yüklü değil veya PATH ortam değişkenine eklenmemiş!
    echo Lütfen Java 21 veya üzerini yükleyin.
    echo https://adoptium.net/ adresinden indirebilirsiniz.
    pause
    exit /b 1
)

REM Maven ile uygulamayı çalıştır
echo Uygulama başlatılıyor, lütfen bekleyin...
mvn compile exec:java -Dexec.mainClass="com.socialplatform.gui.SwingAppLauncher"

REM Hata kontrolü
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Uygulama başlatılırken bir hata oluştu!
    echo Lütfen kullanım kılavuzundaki sorun giderme bölümüne bakın.
    pause
) else (
    echo.
    echo Uygulama başarıyla başlatıldı.
)

pause 