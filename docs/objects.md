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

