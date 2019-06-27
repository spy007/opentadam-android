# Описание объектов

* [PaymentMethod](#PaymentMethod-fields)
* [GpsPosition](#GpsPosition-fields)

<a name="PaymentMethod-fields"></a>
## PaymentMethod

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
kind | string [PaymentMethodKind]() | да | Способ оплаты
id | number | нет | Идентификатор
name | string | нет | Наименование
enoughMoney | boolean | нет | Достаточно ли денег на счету


<a name="GpsPosition-fields"></a>
## GpsPosition

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
lat | number | да | Широта
lon | number | да | Долгота

```json
{
  "lat":34.445335,
  "lon":73.5566567
}
```
