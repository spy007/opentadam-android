# Программа лояльности

`GET /api/client/mobile/1.2/loyalty-program`

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)
* [X-Hive-GPS-Position](http_headrers.md)

## Ответ

#### Тело ответа
* JSON объект [Result](#Result-fields)

<a name="Result-fields"></a>
#### Result

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
lptype | string [LoyaltyType](#LoyaltyType-enum) | да | тип программы лояльности
balance | number | да | Текущий баланс
friends | number | да | Количество друзей в реферальной сети
referralCode | string | нет | Реферальный код для распространения
shareMessage | string | да | Сообщение со ссылкой на приложение

<a name="LoyaltyType-enum"></a>
#### LoyaltyType

Акроним | Описание
--- | ---
none | Программы лояльности не доступны
basic-bonus | Базовая система лояльности
ext-bonus-referral-vip | Продвинутая система лояльности, с бонусами и реферральными системами
