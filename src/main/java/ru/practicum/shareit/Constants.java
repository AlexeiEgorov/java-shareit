package ru.practicum.shareit;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String USER = "User";
    public static final String ITEM = "Item";
    public static final String BOOKING = "Booking";
    public static final String REQUEST = "Request";
    public static final String NO_ACCESS = "No-access";
    public static final String USER_ID_REQ_HEADER = "X-Sharer-User-Id";
    public static final String STATE = "state";
    public static final String SORT_START_PARAM = "start";

    public static final String SORT_CREATED_PARAM = "created";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

}
