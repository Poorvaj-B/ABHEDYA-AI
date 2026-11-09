package com.abhedya.ai;

import com.abhedya.ai.repository.HabitationRepository;
import com.abhedya.ai.repository.HazardZoneRepository;
import com.abhedya.ai.repository.ShelterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class AbhedyaAiApplicationTests {

    @Autowired
    private HabitationRepository habitationRepository;

    @Autowired
    private HazardZoneRepository hazardZoneRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    @Test
    void contextLoads() {
        assertNotNull(habitationRepository, "HabitationRepository should be injected");
        assertNotNull(hazardZoneRepository, "HazardZoneRepository should be injected");
        assertNotNull(shelterRepository, "ShelterRepository should be injected");
    }

    @Test
    void verifyDatabaseMigrationsAndSeedData() {
        long habCount = habitationRepository.count();
        long zoneCount = hazardZoneRepository.count();
        long shelterCount = shelterRepository.count();

        assertEquals(20, habCount, "Flyway seed migration should populate 20 habitations");
        assertTrue(zoneCount >= 6, "Flyway seed migration should populate at least 6 hazard red zones");
        assertTrue(shelterCount >= 7, "Flyway seed migration should populate at least 7 verified shelters");

        long redZoneHabs = habitationRepository.countRedZoneHabitations();
        assertTrue(redZoneHabs > 0, "Should have habitations marked inside Red Zones");
    }
}

