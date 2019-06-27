# Адреса пассажира

`GET /api/client/mobile/3.0/address/client`

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* массив объектов [ClientAddress](objects.md#ClientAddress-fields)
