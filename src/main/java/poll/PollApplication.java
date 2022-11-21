// Main application class, generated by Spring Initializr.
// Must live in the top package so that @SpringBootApplication annotation
// can find all the components.

package poll;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import poll.model.Candidate;
import poll.service.PollService;

@SpringBootApplication
public class PollApplication {

    // The details of the candidates for the poll.
    private static final String[][] candidateDetails = {
            {
                    "Pandion haliaetus",
                    "Osprey",
                    "The osprey, also called sea hawk, river hawk, and fish hawk, is a diurnal, fish-eating bird of prey with a cosmopolitan range."
            },
            {
                    "Elanus leucurus",
                    "White-tailed Kite",
                    "The white-tailed kite is a small raptor found in western North America and parts of South America."
            },
            {
                    "Aquila chrysaetos",
                    "Golden Eagle",
                    "The golden eagle is a bird of prey living in the Northern Hemisphere."
            }
    };

    public static void main(String[] args) {
        SpringApplication.run(PollApplication.class, args);
    }

    // The initDB() @Bean is run automatically on startup, before the
    // @RestController is started.
    // The body could access command line argument args (but doesn't).
    // The fact that initDB() requires a PollService argument tells Spring
    // to auto-configure and pass a PollService instance, which initDB()
    // amends by adding more Poll instances.
    @Bean
    public CommandLineRunner initDB(PollService pollService) {
        return (args) -> {
            // Add all the candidates to the poll service.
            for (String[] candidate : candidateDetails) {
                String scientificName = candidate[0];
                String commonName = candidate[1];
                String description = candidate[2];

                pollService.addCandidate(
                        new Candidate(scientificName, commonName, description)
                );
            }
        };
    }

}
