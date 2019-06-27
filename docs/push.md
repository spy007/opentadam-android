# Регистрация ключа для PUSH уведомлений

Для выполнения данного запроса, пользователь должен быть [авторизован](doc/hmac-auth.md).

`POST /api/client/mobile/1.0/registration/fcm`

**Content-Type**: application/json; charset=utf-8
**Body**: Объект FsmInfo

### Объект FsmInfo

Имя | Тип | Обязательный | Описание
--- | --- | --- | ---
token | string | да | FCM ключ

### Ответ

**Content-Type**: application/json; charset=utf-8

**Body**: Пустой объект.
