# Регистрация в программе лояльности

`POST /api/client/mobile/1.0/loyalty-program`

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)
* [X-Hive-GPS-Position](http_headers.md)

#### Тело запроса
* JSON объект [Params](#Params-fields)

<a name="Params-fields"></a>
#### Params
Имя | Тип | Обязательный | Описание 
--- | --- | --- | --- 
referralCode | string | да | Реферальный код


## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* Пустой JSON объект
