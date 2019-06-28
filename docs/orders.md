# Создание заказа

`POST /api/client/mobile/4.0/orders`

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)

#### Тело запроса
* JSON объект Params

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
paymentMethod	 | Объект [PaymentMethod](objects.md#PaymentMethod-fields) | да | Способ оплаты
tariff | number | да | Идентификатор тарифа
options | array number | да | Выбранные опции тарифа
route | массив объектов [ClientAddress](objects.md#ClientAddress-fields) | да | Маршрут
time | string [LocalDateTime](objects.md#LocalDateTime-fields) | нет | Время заказа (отсутствует, если на текущее)
comment | string | нет | Комментарий к заказу
fixCost | number | нет | Зафиксировать стоимость
useBonuses | number | нет | Количество бонусов для использования в качестве оплаты
disableSms | boolean | нет | Отключить СМС оповещение
disableVoice | boolean | нет | Отключить голосовое оповещение
enablePushUpdates | boolean | нет | Получать обновления состояния заказа через Push

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* JSON объект [Result](#Result-fields)

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
id | number | да | Идентификатор заказа

