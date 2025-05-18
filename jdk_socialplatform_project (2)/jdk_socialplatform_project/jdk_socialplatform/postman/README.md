# Sosyal Platform API Koleksiyonu

Bu koleksiyon, geliştirdiğimiz Sosyal Platform uygulamasının API'lerini test etmek için hazırlanmıştır.

## Koleksiyon İçeriği

* **Kullanıcı İşlemleri**
  - Kullanıcı Oluşturma (POST)
  - Kullanıcı Listesi (GET)
  - Kullanıcı Bilgisi (GET)
  - Email ile Kullanıcı Arama (GET)

* **Arkadaşlık İşlemleri**
  - Arkadaş Ekleme (POST)
  - Arkadaş Silme (DELETE)
  - Arkadaş Listesi (GET)

* **Mesaj İşlemleri**
  - Mesaj Gönderme (POST)
  - Mesaj Geçmişi (GET)
  - Mesajı Okundu İşaretleme (PUT)
  - Okunmamış Mesajlar (GET)

* **Konuşma İşlemleri**
  - Konuşma Listesi (GET)
  - Konuşma Detayı (GET)
  - Kullanıcılar Arası Konuşma (GET)

* **Dış API İşlemleri**
  - Haber Verileri (GET)

## Koleksiyonu Postman'e Yükleme

1. Postman'i açın
2. Sol üst köşedeki "Import" butonuna tıklayın
3. "Upload Files" seçeneğini kullanarak `postman_collection.json` dosyasını seçin
4. Aynı şekilde `environment.json` dosyasını da yükleyin
5. Sağ üstteki ortam seçiciden "SosyalPlatform-Lokal" ortamını seçin

## Kullanım

API testlerini çalıştırmadan önce uygulamanın çalışır durumda olduğundan emin olun. Uygulama varsayılan olarak `http://localhost:8080` adresinde çalışır.

### Değişkenler

Testlerde kullanılan değişkenlerin bazıları:

* `userId`: Test kullanıcısı ID
* `friendId`: Arkadaş ID
* `receiverId`: Mesaj alıcısı ID
* `messageId`: Test mesajı ID
* `conversationId`: Konuşma ID
* `baseUrl`: API adresi

Kullanıcı oluşturduktan sonra aldığınız ID'yi userId değişkenine atayın.

### Örnek Test Akışı

1. Önce "Kullanıcı Oluşturma" ile bir kullanıcı oluşturun
2. Dönen yanıttan ID'yi kopyalayıp Postman'deki userId değişkenine atayın
3. Bir başka kullanıcı daha oluşturun ve ID'sini friendId değişkenine atayın
4. "Arkadaş Ekleme" isteğini gönderin
5. "Arkadaş Listesi" ile arkadaşlığın kurulduğunu doğrulayın
6. "Mesaj Gönderme" ile bir mesaj gönderin
7. "Mesaj Geçmişi" ile mesajın iletildiğini kontrol edin

## HTTP Durum Kodları

* **200 OK**: İşlem başarılı
* **201 Created**: Kaynak oluşturuldu 
* **400 Bad Request**: Hatalı istek
* **401 Unauthorized**: Yetkilendirme gerekli
* **403 Forbidden**: Erişim engellendi
* **404 Not Found**: Kaynak bulunamadı
* **409 Conflict**: Çakışma durumu
* **500 Internal Server Error**: Sunucu hatası

## Notlar

* Arkadaş ekleme işlemi için kullanıcılar arasında arkadaşlık bağı kurulmuş olmalıdır
* Mesaj göndermek için iki kullanıcının arkadaş olması gerekir
* Okunmamış mesaj sayısı profil sayfasında gösterilir
* Hata durumlarında API'nin döndüğü hata mesajlarında sorunun sebebi açıklanır 