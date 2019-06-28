# Редактирование опций заказа

`POST /api/client/mobile/1.0/orders/{id}/options`

где `{id}` – идентификатор заказа

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)

#### Тело запроса
* JSON объект [Params](#Params-fields)

<a name="Params-fields"></a>
#### Params
Имя | Тип | Обязательный | Описание 
--- | --- | --- | --- 
options | array number | mandatory | Выбранные опции тарифа

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### тело ответа
* Пустой


