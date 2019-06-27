# Способы оплаты

`GET /api/client/mobile/1.0/payment-methods`

Запрос работает с [Авторизацией](hmac.md) и без.
В случае использования без авторизации возвращает только один способ оплаты - наличные.

#### HTTP Заголовки
* * **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)
* [X-Hive-GPS-Position](http_headers.md)

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* массив объектов [PaymentMethod](objects.md#PaymentMethod-fields).

