# HMAC авторизация

Для аутентификации использутеся механизм [HMAC-SHA256](https://ru.wikipedia.org/wiki/HMAC).

* [Подписывание запросов](#sign-item)
* [Пример](#example-item)
* [См. также](#links-item)
* [Примеры реализации на](#codeexample-item):
  * [Java 1.8](#codeexample-java-item)
  * [Python](#codeexample-python-item)
  * [PHP](#codeexample-php-item)

<a name="sign-item"></a>
## Подписывание запросов

Каждый запрос, требующий авторизации, должен содержать HTTP заголовки:
* **Date**: текущая дата в формате RFC-1123.
* **Authentication**: `hmac {identity}:{nonce}:{digest}`, где:
  * {identity} – id, полученный в ходе [регистрации](registration.md).
  * {nonce} – некоторое целое число, уникально идентифицирующее запрос на некотором дискретном временном промежутке (например 10 мин). Каждый новый запрос должен сопровождаться новым значением nonce.
  * {digest} – подпись сообщения, вычисляемая как
    `base64encode(hmac("sha256", {secret}, "{method}{path}{date}{nonce}"))`, где
  * {secret} – `base64decode("{key}")` (key получается в ходе регистрации).
  * {method} – метод запроса (например GET).
  * {path} – путь запроса (например /api/client/mobile/1.0/history).
  * {date} – совпадает со значением из заголовка Date.
  * {nonce} – совпадает с описанным выше.

Если авторизация неудачна, то вернется ответ с кодом `401 Not Authorized`.

<a name="example-item"></a>
## Пример

Данные, полученные в ходе регистрации:
* **id**: 1000007750818
* **key**: Jwtm8U6yV9JM3T/GfyUucUD7mRlZJbmLN0FaCrV7BIE=

Данные запроса:
* **method**: GET
* **path**: /api/client/mobile/1.0/history

Заголовки и их значения для включения в запрос:

```
Date: Tue, 24 Jan 2017 16:24:27 +0600
Authentication: hmac 1000007750818:737137758:J8DWmoscR3Z4+YbHvZ0D2Up/8Weh0IjXa26QVb0ihqA=
```

<a name="links-item"></a>
## См. также

* http://restcookbook.com/Basics/loggingin/
* https://tools.ietf.org/html/rfc2104

<a name="codeexample-item"></a>
## Примеры реализации на разных платформах

<a name="codeexample-java-item"></a>
### Java 1.8

```java
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

public class NaiveHmacSigner {

    public static byte[] hmac(String algorithm, byte[] secret, String data)
        throws NoSuchAlgorithmException, InvalidKeyException {

        final Mac mac = Mac.getInstance(algorithm);
        final SecretKeySpec spec = new SecretKeySpec(secret, algorithm);
        mac.init(spec);
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }


    private final String identity;
    private final byte[] secret;

    public NaiveHmacSigner(long id, String key) {
        this.identity = String.valueOf(id);
        this.secret = Base64.getDecoder().decode(key);
    }

    public Map<String, String> newSignature(String method, String path)
        throws InvalidKeyException, NoSuchAlgorithmException {

        final String date = OffsetDateTime.now().format(RFC_1123_DATE_TIME);
        final String nonce = String.valueOf(System.currentTimeMillis());

        final String data = method + path + date + nonce;

        final String digest = Base64.getEncoder().encodeToString(
            hmac("HmacSHA256", this.secret, data)
        );

        final Map<String, String> result = new HashMap<>();
        result.put("Date", date);
        result.put("Authentication", String.format("hmac %s:%s:%s", this.identity, nonce, digest));
        return result;
    }


    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {

        NaiveHmacSigner signer = new NaiveHmacSigner(12312313, "beKXDqRvkrbz+aQpEgn41SSh+9qtLAsb0r2cbcQ24cM=");
        Map<String, String> signature = signer.newSignature("GET", "/api/client/mobile/1.0/history");

        System.out.println(signature);
    }
}
```

<a name="codeexample-python-item"></a>
### Python

```python
from datetime import datetime
import random
import hmac
import hashlib
import base64

MAXINT = 2**32
WEEKDAYS = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"]
MONTHS = ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]


def now_rfc_1123():

    date = datetime.utcnow()
    weekday = WEEKDAYS[date.weekday()]
    month = MONTHS[date.month - 1]
    return "%s, %02d %s %04d %02d:%02d:%02d GMT" % \
           (weekday, date.day, month, date.year, 
            date.hour, date.minute, date.second)


class NaiveHmacSigner:

    def __init__(self, permanent_id, key):

        self.__id = str(permanent_id)
        self.__key = base64.standard_b64decode(key)

    def sign(self, *args):

        line = ''
        for entry in args:
            line = line + entry

        signature_bytes = \
            hmac.new(
                key=self.__key, 
                msg=line.encode('utf-8'), 
                digestmod=hashlib.sha256).digest()
        return base64.standard_b64encode(signature_bytes).decode()

    def new_signature(self, method, uri):

        rand_value = str(random.randint(0, MAXINT))
        date_value = now_rfc_1123()
        auth_value = "hmac %s:%s:%s" % \
                     (self.__id, rand_value, 
                      self.sign(method.upper(), uri, date_value, rand_value))

        return {
            u'Date': date_value,
            u'Authentication': auth_value
        }


c = NaiveHmacSigner(12312313, 'beKXDqRvkrbz+aQpEgn41SSh+9qtLAsb0r2cbcQ24cM=')
print(c.new_signature('GET', '/api/client/mobile/1.0/history'))
```

<a name="codeexample-php-item"></a>
### PHP

```php
<?php

function hmac($algorithm, $secret, $data) {
  return hex2bin(hash_hmac($algorithm, $data, $secret));
}

class NaiveHmacSigner {

  private $identity;

  private $secret;

  public function __construct($id, $key) {
    $this->identity = $id;
    $this->secret = base64_decode($key);
  }

  public function newSignature($requestMethod, $requestPath) {

    $date = date(DATE_RFC1123);
    $nonce = time();

    $data = $requestMethod . $requestPath . $date . $nonce;

    $digest = base64_encode(hmac("sha256", $this->secret, $data));

    return array(
      "Authentication" => "hmac " . $this->identity . ":" . $nonce . ":" . $digest,
      "Date" => $date
    );
  }
}

$signer = new NaiveHmacSigner(12312313, "beKXDqRvkrbz+aQpEgn41SSh+9qtLAsb0r2cbcQ24cM=");
$signature = $signer->newSignature("GET", "/api/client/mobile/1.0/history");

print_r($signature);

?>
```
