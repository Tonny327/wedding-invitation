package com.wedding.invitation.repository;

import com.wedding.invitation.model.Guest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GuestRepositoryTest {

    @Autowired
    private GuestRepository guestRepository;

    @Test
    void save_guest_persistsAndReturnsWithId() {
        Guest guest = new Guest();
        guest.setName("Иван Иванов");
        guest.setAttending(true);
        guest.setComment("Комментарий");

        Guest saved = guestRepository.saveAndFlush(guest);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Иван Иванов");
        assertThat(saved.getAttending()).isTrue();
        assertThat(saved.getComment()).isEqualTo("Комментарий");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findAll_afterSave_returnsSavedGuest() {
        Guest guest = new Guest();
        guest.setName("Мария Петрова");
        guest.setAttending(false);
        guest.setComment(null);

        guestRepository.save(guest);

        List<Guest> all = guestRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getName()).isEqualTo("Мария Петрова");
        assertThat(all.get(0).getAttending()).isFalse();
    }

    @Test
    void findAll_whenEmpty_returnsEmptyList() {
        List<Guest> all = guestRepository.findAll();
        assertThat(all).isEmpty();
    }
}
