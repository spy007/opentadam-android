# Связь с диспетчером

`GET /api/client/mobile/1.0/dispatcher-call`

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)

#### Параметры запроса
Передаются в формате `key=value`

Имя | Тип | Обязательный | Описание 
--- | --- | --- | ---
phone | string | mandatory | Номер телефона, на который должен поступить звонок от диспетчера

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### тело ответа
* Пустой JSON объект

