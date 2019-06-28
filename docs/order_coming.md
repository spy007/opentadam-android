# Уведомление водителя о том, что клиент выходит

`GET /api/client/mobile/1.0/orders/{id}/coming`

где `{id}` – идентификатор заказа

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### тело ответа
* Пустой
