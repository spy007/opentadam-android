# Информация о предоставляемом сервисе

`POST /api/client/mobile/1.1/service`

### HTTP Заголовки

* **Content-Type**: application/json; charset=utf-8
* **Body**: object [Params](#Params-fields).
* [X-Hive-GPS-Position]()

<a name="Params-fields"></a>
## Params

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
paymentMethod | Объект PaymentMethod | нет | Способ оплаты
prevServiceId | string | нет | Идентификатор сервиса из предыдущего запроса

Важно наличие заголовка `X-Hive-GPS-Position` в запросе для точного определения службы такси, обслуживающей регион клиента.

### Ответ

* **Content-Type**: application/json; charset=utf-8
* **Body**: Объект [Service](#Service-fields).

<a name="Service-fields"></a>
#### Service

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
kind | string [ServiceKind](ServiceKind-enum) | да | Тип объекта сервиса
serviceId | string | mandatory, if `kind=service/stub` | Идентификатор сервиса
settings | Объект [Settings](#Settings-fields) | mandatory, if kind=service/stub | Настройки приложения
tariffs | массив объектов Tariff | mandatory, if kind=service | Список тарифов
location | Объект GpsPosition | optional, if kind=service | Координаты региона, в котором предоставляется сервис
message | string | mandatory, if kind=stub | Сообщение о недоступности сервиса
lptype | string [Loyalty](loyalty.md#Loyalty-enum) | mandatory, if kind=service | Тип доступной программы лояльности

<a name="ServiceKind-enum"></a>
#### Акроним ServiceKind

Акроним | Описание
--- | ---
service | Настройки сервиса
stub | Заглушка
same | Информация не изменилась

<a name="Tariff-fields"></a>
#### Объкт Tariff

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
id | number | да | Идентификатор тарифа
name | string | да | Наименование тарифа
icon | string | нет | Идентификатор иконки
description | string | нет | Описание
options | массив объектов [Option](#Option-fields) | да | Список опций
minCost | number | да | Минимальная стоимость заказа
costChangeAllowed | boolean | да | Разрешено ли изменение стоимости заказа
costChangeStep | number | нет | Шаг изменения стоимости
hint | string | нет | Подсказка
showEstimation | boolean | нет | Показывать предрассчет


<a name="Option-fields"></a>
### Объект Option

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
id | number | да | Идентификатор опции
name | string | да | Наименование опции
type | string [OptionValueType](#OptionValueType-enum) | да | Тип стоимости
value | number | да | Стоимость опции
mandatory | boolean | да | Должна ли опция быть включенной по-умолчанию


<a name="OptionValueType-enum"></a>
### Акроним OptionValueType

Акроним | Описание
--- | ---
fixed | Фиксированная стоимость
percent | Процент от стоимости заказа

<a name="Settings-fields"></a>
### Объект Settings

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
cardPaymentAllowed | boolean | да | Доступна ли оплата по банковской карте
dispatcherCall | object [DispatcherCall](#DispatcherCall-fields) | да | Параметры для связи с диспетчерской
mainInterface | string [MainInterfaceMode](MainInterfaceMode-enum) | да | Режим отображения главного интерфейса
geocoding | массив string [GeocodingService](#GeocodingService-enum) | нет | Сервисы геокодинга
currency | объект [Currency](#Currency-fields) | да | Валюта
maps | массив string [MapSource](#MapSource-enum) | да | Типы отображаемой карты
averageSpeed | number | да | Средняя скорость
destinationRequired | boolean | да | Конечный адрес обязателен

<a name="MainInterfaceMode-enum"></a>
### Акроним MainInterfaceMode

Акроним | Описание
--- | ---
simple | Простой
advanced | Сложный
simple-advanced | Сначала простой
advanced-simple | Сначала сложный

<a name="MainInterfaceMode-enum"></a>
### Акроним GeocodingService

Акроним | Описание
--- | ---
google | Google
yandex | Yandex
osm | OpenStreetMap


<a name="MapSource-enum"></a>
### Акроним MapSource

Акроним | Описание
--- | ---
google | Google
yandex | Yandex
osm | OpenStreetMap


<a name="Currency-fields"></a>
### Объект Currency

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
code | string | да | Код валюты
sign | string | да | Символ


<a name="DispatcherCall-fields"></a>
### Объект DispatcherCall

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
allow | string [AllowCall](#AllowCall-enum) | да | Можно ли звонить диспетчеру и как
number | string | нет | Телефон диспетчерской



<a name="AllowCall-enum"></a>
### Акроним AllowCall

Акроним | Описание
--- | ---
direct | Можно позвонить в диспетчерскую напрямую
via server | Можно заказать связь через сервер
no | Связаться с диспетчером нельзя

