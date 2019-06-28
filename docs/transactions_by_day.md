# Список транзакций по дням

`GET /api/client/mobile/1.2/loyalty-program/transactions/by-day`

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)
* [X-Hive-GPS-Position](http_headers.md)

Запрос имеет смысл, только если тип программы лояльности – `ext-bonus-referral-vip`.

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* Массив JSON объектов [ResultDayStat](#ResultDayStat-fields)

<a name="ResultDayStat-fields"></a>
#### ResultDayStat

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
date | string [LocalDate](objects.md#OffsetDateTime-item) | да | Дата
plusBalance | number | да | Денег прибыло
plusBalanceRef | number | да | Денег прибыло от друзей
minusBalance | number | да | Денег потрачено
plusFriends | number | да | Друзей прибыло
