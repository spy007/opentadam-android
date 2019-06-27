# Описание объектов

* [PaymentMethod](#PaymentMethod-fields)
  * [PaymentMethodKind](#PaymentMethodKind-enum)
* [GpsPosition](#GpsPosition-fields)
* [Address](#Address-fields)
  * [AddressComponent](#AddressComponent-fields)
  * [AddressTypes](#AddressTypes-fields)
  * [AddressLevel](#AddressLevel-enum)
* [OffsetDateTime](#OffsetDateTime-item)
* [Cost](#Cost-item)
  * [CostType](#CostType-enum)
  * [CalculationType](#CalculationType-enum)
  * [CostModifier](#CostModifier-fields)
    * [CostModifierType](#CostModifierType-enum)

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

<a name="Address-fields"></a>
## Address

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
name | string | да | Форматированное наименование
components | Массив объектов [AddressComponent](#AddressComponent-fields) | нет | Компоненты
types | Объект [AddressTypes](#AddressTypes-fields) | нет | Идентификатор типа
position | Объект [GpsPosition](#GpsPosition-fields) | нет | Координаты

<a name="AddressComponent-fields"></a>
### AddressComponent

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
level | number [AddressLevel](#AddressLevel-enum) | да | Уровень
name | string | да | Наименование

<a name="AddressTypes-fields"></a>
### AddressTypes

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
pointType | number | нет | Тип точки
aliasType | number | нет | Тип алиаса (тип POI)

<a name="AddressLevel-enum"></a>
### Акроним AddressLevel

Акроним | Описание
--- | ---
0 | Страна
1 | Административный уровень 1 (регион)
2 | Административный уровень 2
3 | Административный уровень 3
4 | Административный уровень 4 (город)
5 | Административный уровень 5
6 | Административный уровень 6 (населенный пункт)
7 | Улица
8 | Дом
9 | Алиас (POI)

### Пример

```json
{
  "name": " Россия, обл Омская, г Омск, б-р Архитекторов, 35 (Мега Омск, семейный торговый центр)",
  "components": [
    {
      "level": 0,
      "name": " Россия"
    }, {
      "level": 1,
      "name": "обл Омская"
    }, {
      "level": 4,
      "name": "г Омск"
    }, {
      "level": 7,
      "name": "б-р Архитекторов"
    }, {
      "level": 8,
      "name": "35"
    }, {
      "level": 9,
      "name": "Мега Омск, семейный торговый центр"
    }
  ],
  "types": {
    "pointType": 63,
    "aliasType": 3
  },
  "position": {
    "lat": 54.972549,
    "lon": 73.28389
  }
}
```

<a name="OffsetDateTime-item"></a>
## OffsetDateTime

Имя | Тип | Описание
---- | --- | --------
OffsetDateTime | string | Формат: `YYYY-MM-DDThh:mm:ss±hh:mm`
LocalDateTime | string | Формат: `YYYY-MM-DDThh:mm:ss`
LocalDate | string | Формат: `YYYY-MM-DD`
Duration | string | Формат: `PnYnMnDTnHnMnS`, Раздел в википедии: [ISO 8601: Durations](https://en.wikipedia.org/wiki/ISO_8601#Durations) Если `n`равно нулю, указывать не обязательно. **Пример:** `PT35M13.106S`


Все форматы соответствуют стандарту [ISO 8601](https://ru.wikipedia.org/wiki/ISO_8601).

<a name="Cost-item"></a>
## Cost
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
type | string [CostType](#CostType-enum) | да | Тип стоимости
amount | number | да | Стоимость
calculation | string [CalculationType](#CalculationType-enum) | да | Тип рассчета
modifier | объект [CostModifier](#CostModifier-fields) | нет | Модификатор стоимости
fixed | number | нет | Зафиксированная стоимость
details | массив объектов [CostItem](#CostItem-fields) | нет | Детали

<a name="CostType-enum"></a>
### Акроним CostType
Акроним | Описание
--- | ---
total | Фиксированная стоимость
approximate | Приблизительная стоимость
minimum | Минимальная стоимость


<a name="CalculationType-enum"></a>
### Акроним CalculationType
Акроним | Описание
--- | ---
fixed | Стоимость в пути не меняется
taximeter | Стоимость зависит от показаний таксометра


<a name="CostModifier-fields"></a>
### CostModifier
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
type | string [CostModifierType](#CostModifierType-enum) | да | Тип модификатора
value | number | да | Значение модификатора


<a name="CostModifierType-enum"></a>
### Акроним CostModifierType
Акроним | Описание
--- | ---
add | Добавление
multiply | Умножение


<a name="CostItem-fields"></a>
### CostItem
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
title | string | да | Наименование
cost | number | да | Стоимость

