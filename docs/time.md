# Текущее время сервера

`GET /api/client/mobile/1.0/time`

## Ответ
Успешный ответ приходит с кодом `200 OK` и содержит:

### Заголовки

| Имя | Тип | Значение |
| --- | --- | --- |
| Content-Type | string | application/json; charset=utf-8 |
| Body | string | Объект [OffsetDateTime](#offset-data-time) |


<a name="offset-data-time"></a>

Имя | Тип | Описание
---- | --- | --------
OffsetDateTime | string | Формат: `YYYY-MM-DDThh:mm:ss±hh:mm`
LocalDateTime | string | Формат: `YYYY-MM-DDThh:mm:ss`
LocalDate | string | Формат: `YYYY-MM-DD`
Duration | string | Формат: `PnYnMnDTnHnMnS`, Раздел в википедии: [ISO 8601: Durations](https://en.wikipedia.org/wiki/ISO_8601#Durations) Если `n`равно нулю, указывать не обязательно. **Пример:** `PT35M13.106S`


Все форматы соответствуют стандарту [ISO 8601](https://ru.wikipedia.org/wiki/ISO_8601).
