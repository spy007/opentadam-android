# Регистрация ключа для PUSH уведомлений

`POST /api/client/mobile/1.0/registration/fcm`

Требуется [Авторизация](hmac.md)
* **Content-Type**: application/json; charset=utf-8
* **Body**: JSON объект [FsmInfo](#FsmInfo-fields)

<a name="FsmInfo-fields"></a>
### Объект FsmInfo

Имя | Тип | Обязательный | Описание
--- | --- | --- | ---
token | string | да | FCM ключ

### Ответ

* **Content-Type**: application/json; charset=utf-8
* **Body**: Пустой объект.
