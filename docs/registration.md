# Регистрация

* [Запрос на регистрацию](#registration-item)
* [Повторная отправка кода подтверждения](#resubmit-item)
* [Подтверждение](#confirm-item)

<a name="registration-item"></a>
## Запрос на регистрацию

`POST /api/client/mobile/1.0/registration/submit`

#### Заголовки
* [Hive-Profile](http_headers.md)
* [X-Hive-GPS-Position](http_headers.md)

### Тело запроса

JSON объект [SubmitRequest](#registration-submit-fields)

<a name="registration-submit-fields"></a>
#### Поля JSON объекта **SubmitRequest**

Имя | Тип | Обязательный | Описание
--- | --- | --- | ---
confirmationType | string [ConfirmationType](#ConfirmationType-enum) | нет | Как будет отправлен код подтверждения
phone | string | да | Номер телефона
info | Объект [ClientInfo](#ClientInfo-fields) | нет | Информация о клиенте
referralCode | string | нет | Реферальный код

<a name="ConfirmationType-enum"></a>
#### Значения параметра ConfirmationType

Акроним | Описание
--- | ---
sms | По СМС
voice | По СМС

<a name="ClientInfo-fields"></a>
#### Описание ClientInfo

Имя | Тип | Обязательный | Описание
--- | --- | --- | ---
lastName | string | нет | Фамилия
firstName	| string | нет | Имя
middleName | string | нет | Отчество
gender | number [Gender](#Gender-enum) |	нет | Пол
birthDate |	string OffsetDateTime | нет | Дата рождения

<a name="Gender-enum"></a>
#### Значения параметра Gender

Акроним | Описание
--- | ---
0 | Мужской
1 | Женский

### Ответ

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


<a name="resubmit-item"></a>
## Повторная отправка кода подтверждения

`GET /api/client/mobile/1.0/registration/resubmit`

#### Заголовки
* [Hive-Profile](http_headers.md)

### Параметры запроса
Передаются в формате `key=value`

Имя | Тип | Обязательный | Описание 
--- | --- | --- | ---
id | number | да | Идентификатор, полученный при запросе [регистрации](#registration-item)
confirmationType | string | нет | Как будет отправлен код подтверждения `(“sms”/“voice”, по-умолчанию – “sms”)`

### Ответ

Пустой

### Коды ошибок

* **-10007** - Невалидный идентификатор клиента


<a name="confirm-item"></a>
## Подтверждение

`GET /api/client/mobile/1.0/registration/confirm`

#### Заголовки
* [Hive-Profile](http_headers.md)

### Параметры запроса
Передаются в формате `key=value`

Имя | Тип | Обязательный | Описание 
--- | --- | --- | ---
id | number | да | Идентификатор, полученный при [регистрации](#registration-item)
code | string | да | Код подтверждения

### Ответ

* **Content-Type**: application/json; charset=utf-8
* **Body**: Объект [Confirmed](#Confirmed-fields).

<a name="Confirmed-fields"></a>
#### Объект Confirmed

Имя | Тип | Обязательный | Описание 
--- | --- | --- | ---
id | number | да | Постоянный идентификатор клиента (identity)
key | string | да | Закодированный в base64 секретный ключ (secret)

#### Коды ошибок

* **-10007** - Невалидный идентификатор клиента
* **-10008** - Невалидный код подтверждения




