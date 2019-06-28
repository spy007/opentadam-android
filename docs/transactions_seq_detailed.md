Список транзакций, детальный

`GET /api/client/mobile/1.2/loyalty-program/transactions/seq-detailed`

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)
* [X-Hive-GPS-Position](http_headers.md)

#### Параметры запроса
Передаются в формате `key=value`

Имя | Тип | Обязательный | Описание 
--- | --- | --- | ---
oft | number | нет | Смещение в истории транзакций

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* Массив JSON объектов [Transaction](#Transaction-fields)

<a name="Transaction-fields"></a>
#### Transaction

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
date | string [OffsetDateTime](objects.md#OffsetDateTime-item) | mandatory | Дата
amount | number | mandatory | Размер и направление платежа(положительное число для начислений, отрицательное для списаний)
balance | number | mandatory | Остаток на счете по завершению транзакции
typ | number [PaymentType](#PaymentType-enum) | mandatory | Тип платежа
oft | number | mandatory | Смещение в истории платежей относительно начала

<a name="PaymentType-enum"></a>
#### PaymentType
Акроним | Описание
--- | ---
11 | Начисление бонуса клиенту за совершение поездки
12 | Начисление бонуса водителю за совершение поездки
13 | Начисление бонуса рефералу клиента за совершение поездки
14 | Начисление бонуса рефералу водителя за совершение поездки
23 | Списание бонусов за поездку
24 | Начисление бонусов вручную, по усмотрению службы такси
25 | Списание бонусов вручную, по усмотрению службы такси
