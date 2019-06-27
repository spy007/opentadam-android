# Поиск адреса по координатам

`GET /api/client/mobile/2.0/address/nearest`

## Ответ

* **Content-Type**: application/json; charset=utf-8
* **Body**: массив объектов [Address](objects.md#address-fields).

Список будет непустым только при наличии заголовка `X-Hive-GPS-Position` в запросе.
