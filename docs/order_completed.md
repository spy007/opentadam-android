# Подробности по завершенному заказу

`GET /api/client/mobile/1.0/orders/{id}/completed`

где `{id}` – идентификатор заказа

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)

## Ответ

#### HTTP Заголовки

#### Тело ответа
* JSON ответ [Result](#Result-fields)

<a name="Result-fields"></a>
#### Result
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
route | массив объектов [Address](objects.md#Address-fields) | да | Маршрут заказа
completionDate | string [OffsetDateTime](objects.md#OffsetDateTime-item) | да | Дата завершения заказа
check | массив объектов [ResultCheckItem](#ResultCheckItem-fields) | да | Позиции чека
total | number | да | Итого
usedBonuses | number | да | Количество бонусов, использованных в качестве оплаты
toPay | number | да | К оплате


<a name="ResultCheckItem-fields"></a>
#### ResultCheckItem
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
title | string | да | Наименование
cost | number | да | Стоимость

