# Получение подробностей по заказу

`GET /api/client/mobile/2.2/orders/{id}`

где `{id}` - идентификатор заказа

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)


## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* Объект [OrderInfo](#OrderInfo-fields)

<a name="OrderInfo-fields"></a>
#### OrderInfo

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
state | number [OrderState](#OrderState-enum) | true | Статус заказа
route | массив объектов [ClientAddress](objects.md#ClientAddress-fields) | true | Маршрут заказа
assignee | объект [Assignee](#Assignee-fields) | false | Назначенный водитель
options | массив объектов [Option](#Option-fields) | true | Тарифные опции
time | string [OffsetDateTime](objects.md#OffsetDateTime-item) | false | Время заказа (отсутствует, если на текущее)
needsProlongation | boolean | true | Необходимо продлить заказ
comment | string | false | Комментарий
distance | number | false | Расстояние
cost | object Cost | true | Стоимость
executionTime | string [Duration](objects.md#OffsetDateTime-item) | false | Продолжительность поездки (только для статуса 4)
usedBonuses | number | false | Количество бонусов для использования в качестве оплаты
paymentMethod | объект [PaymentMethod](objects.md#PaymentMethod-fields) | false | Способ оплаты
costFixAllowed | boolean | true | Разрешена ли фиксация стоимости заказа
isComing | boolean | false | Уведомил ли клиент о том, что выходит


<a name="OrderState-enum"></a>
#### OrderState
Акроним | Описание
--- | ---
1 | Создан
2 | Назначен
3 | По адресу
4 | Выполняется
5 | Завершен
6 | Отменен


<a name="Assignee-fields"></a>
#### Assignee

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
car | объект [Car](#Car-fields) | да | Автомобиль
location | объект [GpsPosition](objects.md#GpsPosition-fields) | нет | Координаты
call | объект [AssigneeCall](#AssigneeCall-fields) | да | Параметры для связи с водителем


<a name="Car-fields"></a>
#### Car
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
alias | string | да | Краткое наименование
brand | string | да | Брэнд
model | string | да | Модель
color | string | да | Цвет
regNum | string | да | Регистрационный номер
regNumMask | объект [RegNumMask](#RegNumMask-fields) | да | Маска регистрационного номера


<a name="RegNumMask-fields"></a>
#### RegNumMask
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
name | string | да | Наименование маски
countryId | number | да | Идентификатор страны
regexp | string | да | Регулярное выражение
format | string | да | Форматная строка


<a name="AssigneeCall-fields"></a>
#### AssigneeCall
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
allow | string [AllowCall](#AllowCall-enum) | true | Можно ли звонить водителю и как
numbers | array string | false | Список номеров водителя



<a name="AllowCall-enum"></a>
#### AllowCall
Акроним | Описание
--- | ---
direct | Можно позвонить водителю напрямую
via server | Можно заказать связь через сервер
no | Связаться с водителем нельзя



<a name="Option-fields"></a>
#### Option
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
id | number | да | Идентификатор опции
name | string | да | Наименование опции
type | string [OptionValueType](#OptionValueType-enum) | да | Тип стоимости
value | number | да | Стоимость опции
selected | boolean | да | Выбрана ли опция



<a name="OptionValueType-enum"></a>
#### OptionValueType
Акроним | Описание
--- | ---
fixed | Фиксированная стоимость
percent | Процент от стоимости заказа

