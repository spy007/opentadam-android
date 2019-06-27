# Регистрация

`POST /api/client/mobile/1.0/registration/submit`

## Заголовки

Hive-Profile, X-Hive-GPS-Position

## Тело запроса

JSON объект [SubmitRequest](#registration-submit-fields)

<a name="registration-submit-fields"></a>
### Поля JSON объекта **SubmitRequest**

Имя | Тип | Обязательный | Описание
--- | --- | --- | ---
confirmationType | string [ConfirmationType](#ConfirmationType-enum) | нет | Как будет отправлен код подтверждения
phone | string | да | Номер телефона
info | Объект [ClientInfo](#ClientInfo-fields) | нет | Информация о клиенте
referralCode | string | нет | Реферальный код

<a name="ConfirmationType-enum"></a>
### Значения параметра ConfirmationType

Акроним | Описание
--- | ---
sms | По СМС
voice | По СМС

<a name="ClientInfo-fields"></a>
### Описание ClientInfo

Имя | Тип | Обязательный | Описание
--- | --- | --- | ---
lastName | string | нет | Фамилия
firstName	| string | нет | Имя
middleName | string | нет | Отчество
gender | number [Gender](#Gender-enum) |	нет | Пол
birthDate |	string OffsetDateTime | нет | Дата рождения

<a name="Gender-enum"></a>
### Значения параметра Gender

Акроним | Описание
--- | ---
0 | Мужской
1 | Женский

## Ответ

В теле ответа передается JSON оъект Submitted

```json
{
 "Submitted": {
    "id": 121231234124
    }
}
```

<a name="Submitted-fields"></a>

Имя | Тип | Обязательный | Описание
--- | --- | --- | ---
id | number | да | Временный идентификатор клиента


### Примечание
Одновременно с ответом на телефон, указанный в запросе, отправляет смс с кодом подтвержения

