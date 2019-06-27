# Погашение долга

`GET /api/client/mobile/1.0/debt-paid`

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)
* [X-Hive-GPS-Position](http_headers.md)

#### Параметры запроса
Передаются в формате `key=value`

Имя | Тип | Обязательный | Описание 
--- | --- | --- | ---
cardId | number | да | Идентификатор карты клиента, которая будет использована для погашения долга

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
пустой объект
