# Поиск адреса по координатам

`GET /api/client/mobile/2.0/address/nearest`

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)
* [X-Hive-GPS-Position](http_headers.md)

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* массив объектов [Address](objects.md#address-fields).

Список будет непустым только при наличии заголовка `X-Hive-GPS-Position` в запросе.
