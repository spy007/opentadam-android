# Удаление банковской карты

`DELETE /api/client/mobile/1.0/payment-methods/{card_id}`

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)

#### Параметры запроса
Передаются в формате `key=value`

Имя | Тип | Обязательный | Описание 
--- | --- | --- | ---
card_id | number | да | ID платежного средства (только для банковских карт)

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
пустой JSON объект
