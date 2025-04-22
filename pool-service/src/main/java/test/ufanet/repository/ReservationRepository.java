package test.ufanet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import test.ufanet.model.Client;
import test.ufanet.model.Reservation;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Посчитать кол-во записей в конкретное время
    @Query("""
            SELECT count(r)
            FROM Reservation r
            WHERE r.startTime <= :startTime AND r.endTime > :startTime
            """)
    int countByStartTime(@Param("startTime") OffsetDateTime startTime);

    // Проверка, что у клиента есть уже запись в этот день
    @Query("""
            SELECT COUNT(r) > 0
            FROM Reservation r
            WHERE r.client = :client AND DATE(r.startTime) = :day
            """)
    boolean existsForClientAndDay(@Param("client") Client client,
                                 @Param("day") LocalDate day);

    // Найти бронирование по id клиента и id заказа
    Optional<Reservation> findByClient_IdAndOrderId(Long clientId, UUID orderId);

    // Получить записи на определенную дату
    @Query("""
            SELECT r
            FROM Reservation r
            WHERE DATE(r.startTime) = :date
            """)
    List<Reservation> findAllByDay(@Param("day") LocalDate day);

}
