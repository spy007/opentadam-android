# Фиксация стоимости заказа

`GET /api/client/mobile/1.0/orders/{id}/fix-cost`

где `{id}` – идентификатор заказа

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)

#### Параметры запроса
Передаются в формате `key=value`

Имя | Тип | Обязательный | Описание 
--- | --- | --- | ---
amount | number | true | Фиксированная стоимость

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### тело ответа
* Пустой JSON объект
