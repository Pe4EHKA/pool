INSERT INTO working_schedule (day_of_week, start_time, end_time)
VALUES (1, '08:00', '21:00'),
       (2, '08:00', '21:00'),
       (3, '08:00', '21:00'),
       (4, '08:00', '21:00'),
       (5, '08:00', '21:00'),
       (6, '08:00', '21:00'),
       (7, '08:00', '21:00')
ON CONFLICT (day_of_week)
DO UPDATE SET
        start_time=EXCLUDED.start_time,
        end_time=EXCLUDED.end_time;



INSERT INTO schedule_exception (exception_date, is_working, start_time, end_time)
VALUES (DATE '2025-09-05', TRUE, '12:00', '19:00'),
       (DATE '2025-04-09', TRUE, '12:00', '19:00')
ON CONFLICT (exception_date)
DO UPDATE SET
        is_working=EXCLUDED.is_working,
        start_time=EXCLUDED.start_time,
        end_time=EXCLUDED.end_time;