# SOSYAL PLATFORM: ARKADAŞLIK VE MESAJLAŞMA SİSTEMİ

## PROJE HAKKINDA
Bu proje, kullanıcıların kayıt olabileceği, arkadaş ekleyebileceği ve mesajlaşabileceği 
modern bir sosyal ağ uygulamasıdır. Spring Boot ve Java Swing teknolojileri kullanılarak 
geliştirilmiştir.

## GRUP ÜYELERİ
- Ahmet KARGI
- Muhammed Akif YILDIZ
- Furkan TOPÇU

## SİSTEM GEREKSİNİMLERİ
- Windows 10 veya Windows 11 işletim sistemi (macOS da desteklenmektedir)
- Java 21 veya üzeri
- Maven 3.6 veya üzeri
- 2GB boş RAM
- 500MB disk alanı

## KURULUM
1. Java 21'i yükleyin (https://adoptium.net/ adresinden indirilebilir)
2. Maven'ı yükleyin (https://maven.apache.org/download.cgi)
3. Projeyi ZIP olarak indirin veya git ile klonlayın
4. Komut isteminde proje dizinine gidin

## BAĞIMLILIKLARI YÜKLEME
```
mvn clean install
```

## PROJEYİ ÇALIŞTIRMA

### Windows Kullanıcıları İçin:
1. Swing arayüzünü başlatmak için proje klasöründeki `run-swing-app.bat` dosyasını çift tıklayın.

VEYA

Komut satırı ile:
```
mvn compile exec:java -Dexec.mainClass="com.socialplatform.gui.SwingAppLauncher"
```

### Spring Boot Web Uygulaması Olarak Çalıştırma:
```
mvn spring-boot:run
```

### JAR Dosyası Oluşturup Çalıştırma:
```
mvn package
java -jar target/social-platform-1.0-SNAPSHOT.jar
```

## API VE SWING ARAYÜZÜ
- Swing arayüzü kullanıcı dostu bir grafik arayüzü sunmaktadır.
- API'ye http://localhost:8080 adresinden erişilebilir.
- API dokümantasyonu için: http://localhost:8080/swagger-ui.html
- API testleri için Postman koleksiyonu `/postman` klasöründe bulunmaktadır.

## DAHA FAZLA BİLGİ
Detaylı kullanım bilgileri ve test senaryoları için lütfen aşağıdaki dökümanlara bakın:
- `/documents/kullanim_kilavuzu.md` - Detaylı kullanım kılavuzu
- `/documents/test_senaryolari.md` - Test senaryoları
- `/postman/README.md` - API test bilgileri

## İLETİŞİM
Proje hakkında sorularınız için lütfen ekip üyeleriyle iletişime geçin.

---
