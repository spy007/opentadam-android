# Предварительная оценка заказа

`GET /api/client/mobile/3.0/estimate`

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)

#### Тело запроса
* JSON объект [Params](#Params-fields)

<a name="Params-fields"></a>
#### Params

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
paymentMethod | Объект [PaymentMethod](objects.md#PaymentMethod-fields) | да | Способ оплаты
tariffs | массив number | да | Массив тарифов (не более 4х за раз)
options | массиа number | да | Выбранные опции тарифа
route | массив объектов [GpsPosition](objects.md#GpsPosition-fields) | да | Точки маршрута
time | string LocalDateTime | нет | Время заказа (отсутствует, если на ближайшее время)

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* JSON объект [Estimations](#Estimation-fields)

<a name="Estimations-fields"></a>
#### Estimations
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
estimations | массив Estimation | да | Список предварительных оценок стоимости по заказу
path | Объект GeoJSON::LineString | нет | Предполагаемый подробный маршрут поездки
distance | number | нет | Расстояние в метрах

<a name="Estimation-fields"></a>
#### Estimation
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
tariff | number | да | ID тарифа, по которому производилась оценка стоимости
cost | Объект [Cost](objects.md#Cost-item) | да | Стоимость
submissionTime | number | нет | Предполагаемое минимальное время подачи машины (в секундах)


