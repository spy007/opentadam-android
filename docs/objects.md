# Описание объектов

* [PaymentMethod](#PaymentMethod-fields)
* [GpsPosition](#GpsPosition-fields)

<a name="PaymentMethod-fields"></a>
## PaymentMethod

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
kind | string [PaymentMethodKind](#PaymentMethodKind-enum) | да | Способ оплаты
id | number | нет | Идентификатор
name | string | нет | Наименование
enoughMoney | boolean | нет | Достаточно ли денег на счету

Если `kind=cash`, то поля `id` и `name` будут отсутствовать.
Если `kind=contractor`, то поля `id` и `name` будут присутствовать и означать идентификатор и наименование контрагента соответственно; поле `enoughMoney` так же будет присутствовать, но только для данного запроса (в подробностях по заказу, например, оно будет отсутствовать).
Если `kind=credit_card`, то поля `id` и `name` будут присутствовать и означать идентификатор и PAN банковской карты.

<a name="PaymentMethodKind-enum"></a>
### Акроним PaymentMethodKind

Акроним | Описание
--- | ---
cash | Наличные
contractor | Контрагент
credit_card | Банковская карта


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
