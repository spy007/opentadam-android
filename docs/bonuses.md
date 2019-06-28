# Доступные бонусы

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)
* [X-Hive-GPS-Position](http_headrers.md)

Важно наличие заголовка `X-Hive-GPS-Position` в запросе для точного определения службы такси, обслуживающей регион клиента.

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* JSON объект [Bonuses](#Bonuses-fields)

<a name="Bonuses-fields"></a>
#### Bonuses

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
balance | number | да | Количество бонусов
capabilities | Объект [Capabilities](#Capabilities-fields) | нет | Возможности по оплате бонусами


<a name="Capabilities-fields"></a>
#### Capabilities

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
type | string [CapabilitiesType](#CapabilitiesType-enum) | true | Тип возможностей
min | number | Если type=min-max | Минимальная сумма
max | number | Если type=min-max | Максимальная сумма
options | array number | Если type=options | Варианты сумм


<a name="CapabilitiesType-enum"></a>
#### Акроним CapabilitiesType

Акроним | Описание
--- | ---
min-max | Минимальная и максимальная границы использования бонусов
options | Варианты количества бонусов для оплаты
