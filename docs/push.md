# Регистрация ключа для PUSH уведомлений

`POST /api/client/mobile/1.0/registration/fcm`

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: application/json; charset=utf-8

#### Тело запроса
* JSON объект [FsmInfo](#FsmInfo-fields)

<a name="FsmInfo-fields"></a>
### Объект FsmInfo

Имя | Тип | Обязательный | Описание
--- | --- | --- | ---
token | string | да | FCM ключ

### Ответ

#### HTTP Заголовки
* **Content-Type**: application/json; charset=utf-8

#### Тело ответа
Пустой объект.
