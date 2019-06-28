# Получение списка заказов

`GET /api/client/mobile/2.0/orders`

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* Массив объектов [ShortOrderInfo](#ShortOrderInfo-fields)

<a name="ShortOrderInfo-fields"></a>
#### ShortOrderInfo

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
id | number | да | Идентификатор заказа
state | number [OrderState](#OrderState-enum) | да | Статус заказа
route | массив объектов [Address](objects.md#Address-fields) | да | Маршрут заказа
assignee | object [Assignee](#Assignee-fields) | нет | Назначенный водитель
time | string [OffsetDateTime](objects.md#OffsetDateTime-item) | нет | Время заказа (отсутствует, если на текущее)
needsProlongation | boolean | да | Необходимо продлить заказ


<a name="OrderState-enum"></a>
#### OrderState
Акроним | Описание
--- | ----
1 | Создан
2 | Назначен
3 | По адресу
4 | Выполняется


<a name="Assignee-fields"></a>
#### Assignee
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
car | Объект [Car](#Car-fields) | да | Автомобиль


<a name="Car-fields"></a>
#### Car
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
brand | string | true | Брэнд
model | string | true | Модель
color | string | true | Цвет
regNum | string | true | Регистрационный номер
regNumMask | Объект [RegNumMask](#RegNumMask-fields) | true | Маска регистрационного номера


<a name="RegNumMask-fields"></a>
#### RegNumMask
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
format | string | true | Форматная строка




