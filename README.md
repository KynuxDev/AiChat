# Minecraft AI Chat Eklentisi - KynuxCloud Entegrasyonu

Minecraft sunucunuzda yapay zeka ile sohbet etmenizi sağlayan ChatService eklentisi, KynuxCloud altyapısı ve API servisleri üzerinde çalışmaktadır. Bu profesyonel eklenti, KynuxCloud'un güçlü yapay zeka platformunun tüm avantajlarını Minecraft sunucunuza entegre ederek, oyuncuların `/ai` komutuyla gelişmiş yapay zeka modellerinden anında yanıt almalarını sağlar.

## KynuxCloud Yapay Zeka Platformu Hakkında

KynuxCloud, çeşitli yapay zeka modellerini tek bir API altında toplayarak geliştiricilere kolaylık sağlayan yenilikçi bir platformdur. ChatService eklentisi, aşağıdaki özelliklere sahip KynuxCloud altyapısı üzerinde çalışır:

- **Çoklu Model Desteği**: OpenAI (GPT-4o, GPT-4, GPT-4 Turbo), Anthropic (Claude 3 Opus, Claude 3 Haiku, Claude 3 Sonnet), Google (Gemini 2.0) ve Cohere (Command R) gibi yapay zeka modellerine erişim
- **Yüksek Performans**: Optimize edilmiş API yapısı sayesinde düşük gecikme süreleri
- **Güvenlik**: Endüstri standardı güvenlik protokolleri
- **Kolay Entegrasyon**: Basit ve anlaşılır API yapısı

KynuxCloud API servisleri hakkında daha fazla bilgi için [api.kynux.cloud](http://api.kynux.cloud/api-docs) adresini ziyaret edebilirsiniz.

## Eklenti Özellikleri

- **Kolay Kullanım**: `/ai <mesaj>` komutu ile anında AI asistana erişim
- **Kişiselleştirilmiş Deneyim**: Oyuncu bazlı konuşma geçmişi yönetimi
- **Yüksek Performans**: Asenkron API iletişimi sayesinde sunucu performansını etkilemez
- **Geniş Model Seçenekleri**: KynuxCloud üzerinden erişilebilen tüm gelişmiş yapay zeka modelleri:
  - OpenAI: GPT-4.5, GPT-4o, GPT-4, GPT-4 Turbo
  - Anthropic: Claude 3 Opus, Claude 3.5 Sonnet, Claude 3 Haiku, Claude 3.7 Sonnet
  - Google: Gemini 2.0 Pro, Gemini 2.0 Flash
  - Cohere: Command R, Command R Plus
  - ve daha fazlası (`/ai models` komutu ile güncel listeyi görebilirsiniz)
- **Kapsamlı Yönetim**: Admin komutları (/ai reload, /ai models, /ai clearall)
- **Özelleştirilebilir Görünüm**: Renkli ve özelleştirilebilir AI mesaj önekleri

## Komutlar

- `/ai <mesaj>` - AI'ya mesaj gönderir ve yanıt alır
- `/ai clear` - Kendi konuşma geçmişinizi temizler
- `/ai clearall` - Tüm konuşma geçmişlerini temizler (admin izni gerektirir)
- `/ai reload` - Yapılandırmayı yeniden yükler (admin izni gerektirir)
- `/ai models` - Kullanılabilir AI modellerini listeler (admin izni gerektirir)

## Kurulum

1. JAR dosyasını sunucunuzun plugins klasörüne yükleyin
2. Sunucuyu yeniden başlatın veya `/reload` komutunu çalıştırın
3. `/plugins/ChatService/config.yml` dosyasından API anahtarınızı ayarlayın
4. `/reload` komutu ile yapılandırmayı yeniden yükleyin

## Yapılandırma (config.yml)

```yaml
api:
  url: 'http://api.kynux.cloud/api/v1/chat/completions'
  key: 'YOUR_API_KEY'  # KynuxCloud API anahtarınızı buraya ekleyin
  model: 'gpt-4o'      # Tercih ettiğiniz yapay zeka modeli 
  temperature: 0.7     # Yaratıcılık seviyesi (0.0-1.0)
  max_tokens: 500      # Maksimum yanıt uzunluğu

chat:
  system_prompt: 'Sen yardımcı bir asistansın. Minecraft oyuncusuna kısa ve öz yanıtlar ver.'
  prefix: '&b[AI] &r'  # AI yanıtlarının öneki (renk kodları desteklenir)
```

### KynuxCloud API Endpointleri

- **Chat Completion**: `http://api.kynux.cloud/api/v1/chat/completions`
- **Model Listesi**: `http://api.kynux.cloud/api/v1/models`

API konusunda daha detaylı bilgi ve dokümantasyon için [developer.kynux.cloud](https://developer.kynux.cloud) adresini ziyaret edebilirsiniz.

## İzinler

- `chatservice.ai` - `/ai` komutunu kullanma izni (varsayılan olarak herkese verilir)
- `chatservice.admin` - Admin komutlarını kullanma izni

## Geliştirme

Bu eklenti Maven ile geliştirilmiştir. Projeyi geliştirmek için:

1. Projeyi klonlayın
2. Maven ile bağımlılıkları yükleyin
3. Kodunuzu yazın
4. `package` komutu ile JAR dosyasını oluşturun

## Teknik Özellikler

- **Modern Teknolojiler**: OkHttp3 ile güvenli ve hızlı API istekleri
- **Verimli İşleme**: Jackson kütüphanesi ile optimum JSON ayrıştırma
- **Yüksek Performans**: CompletableFuture kullanarak tamamen asenkron işlem akışı
- **Minecraft Entegrasyonu**: Spigot API ile tam uyumluluk
- **Güvenlik Önlemleri**: API anahtarı ve iletişim güvenliği için gelişmiş önlemler

## Önemli Notlar ve Güvenlik

- **API Anahtarı Güvenliği**: API anahtarınızı kesinlikle gizli tutun ve `config.yml` dosyasındaki anahtarı kimseyle paylaşmayın.
- **Kullanım Limitleri**: KynuxCloud API kullanımı, hesabınızın paketine göre belirli limitlere tabidir. Ayrıntılar için [Discord Sunucusundan](https://discord.gg/wCK5dVSY2n) destek alabilirsiniz.
- **Güncellemeler**: KynuxCloud'un düzenli olarak yeni yapay zeka modelleri eklediğini unutmayın. Güncel model listesi için `/ai models` komutunu kullanabilirsiniz.

---

Bu eklenti, KynuxCloud'un resmi desteğiyle geliştirilmiştir. Sorularınız, geri bildirimleriniz veya destek talepleriniz için [support@kynux.cloud](mailto:support@kynux.cloud) adresinden bizimle iletişime geçebilirsiniz.

© 2025 KynuxCloud - Tüm hakları saklıdır.
