# Cостояние клиентского счета

`GET /api/client/mobile/1.0/account`

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)
* [X-Hive-GPS-Position](http_headers.md)

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* JSON объект [AccountState](#AccountState-fields)

<a name="AccountState-fields"></a>
#### AccountState

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
balance | number | да | Текущий баланс
currency | number | да | Код валюты согласно [ISO 4217](https://ru.wikipedia.org/wiki/ISO_4217)

