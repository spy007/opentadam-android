# Справочник стран

`GET /api/client/mobile/1.0/countries`

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* Массив JSON объектов [Country](#Country-fields)

<a name="Country-fields"></a>
#### Country
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
name | string | да | Название
isoCode | string | да | Код по ISO 3166-1 alpha-2
phoneCode | string | да | Телефонный код
phoneMask | string | нет | Телефонная маска
