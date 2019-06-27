# Ближайшие водители

`POST /api/client/mobile/2.0/drivers`

* **Content-Type**: application/json; charset=utf-8
* **Body**: object [Params](#params-fields)

<a name="params-fields"></a>
### Объект Params

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
paymentMethod | Объект [PaymentMethod](objects.md#PaymentMethod-fields) | да | Способ оплаты
tariff | number | да | Идентификатор тарифа

## Ответ

**Content-Type**: application/json; charset=utf-8
**Body**: array object [Driver](#driver-fields).

Список будет непустым только при наличии заголовка `X-Hive-GPS-Position` в запросе.

<a name="driver-fields"></a>
### Объект Driver

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
id | number | да | Идентификатор водителя
location | Объект [GpsPosition](objects.md#GpsPosition-fields) | да | Координаты водителя




