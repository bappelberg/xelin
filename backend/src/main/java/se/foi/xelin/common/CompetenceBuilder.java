package se.foi.xelin.common;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Random;

@Component
public class CompetenceBuilder {

    private int xpPoints = 0;
    private final Random random = new Random();

    // En lista med påminnelser om att felsökning = lärande
    private final List<String> reminders = List.of(
        "Att leta buggar är det absolut snabbaste sättet att bli en mästare på Spring Boot!",
        "Kör du fast? Det är då hjärnan bygger nya synapser. Du blir smartare just nu!",
        "Docker-strul? Grattis, du bygger ovärderlig DevOps-kompetens inför framtiden!",
        "Varje felmeddelande du löser idag är ett problem du slipper brottas med imorgon.",
        "Kom ihåg: En duktig utvecklare är bara en utvecklare som har misslyckats fler gånger än nybörjaren ens har försökt."
    );

    // Tickar var 15:e minut (900 000 millisekunder)
    @Scheduled(fixedRate = 900000)
    public void boostCompetence() {
        xpPoints += 100;
        String randomReminder = reminders.get(random.nextInt(reminders.size()));

        System.out.println("\n======================================================================");
        System.out.println("LOGG: [CompetenceBuilder] 💡 KOMPETENS-BOOST!");
        System.out.println("-> " + randomReminder);
        System.out.println("-> Total intjänad utvecklar-XP i denna session: " + xpPoints + " XP");
        System.out.println("======================================================================\n");
    }
}