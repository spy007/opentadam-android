# Текущее время сервера

`GET /api/client/mobile/1.0/time`

#### HTTP Заголовки
* [Hive-Profile](http_headers.md)

## Ответ
Успешный ответ приходит с кодом `200 OK` и содержит:

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* JSON объект [OffsetDateTime](objects.md#OffsetDateTime-item)
